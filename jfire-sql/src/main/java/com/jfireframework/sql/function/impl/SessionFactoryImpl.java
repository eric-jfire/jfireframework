package com.jfireframework.sql.function.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import com.jfireframework.baseutil.PackageScan;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.bean.annotation.field.CanBeNull;
import com.jfireframework.sql.annotation.BatchUpdate;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.Query;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.annotation.Update;
import com.jfireframework.sql.dbstructure.MariaDBStructure;
import com.jfireframework.sql.dbstructure.Structure;
import com.jfireframework.sql.function.Dao;
import com.jfireframework.sql.function.ResultMap;
import com.jfireframework.sql.function.SessionFactory;
import com.jfireframework.sql.function.SqlSession;
import com.jfireframework.sql.function.mapper.Mapper;
import com.jfireframework.sql.util.MapperBuilder;

public class SessionFactoryImpl implements SessionFactory
{
    @Resource
    protected DataSource                        dataSource;
    @Resource
    @CanBeNull
    protected ClassLoader                       classLoader;
    protected static ThreadLocal<SqlSession>    sessionLocal = new ThreadLocal<SqlSession>();
    protected String                            scanPackage;
    // 如果值是create，则会创建表。
    protected String                            tableMode    = "none";
    // 当前支持的类型有mysql,MariaDB
    protected String                            dbType;
    protected IdentityHashMap<Class<?>, Mapper> mappers      = new IdentityHashMap<Class<?>, Mapper>(128);
    protected IdentityHashMap<Class<?>, Dao<?>> daos         = new IdentityHashMap<Class<?>, Dao<?>>();
    protected Map<Class<?>, ResultMap<?>>       resultMaps   = new IdentityHashMap<Class<?>, ResultMap<?>>();
    
    public SessionFactoryImpl()
    {
        
    }
    
    public SessionFactoryImpl(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
    
    @PostConstruct
    public void init()
    {
        if (dataSource == null)
        {
            return;
        }
        Set<String> set = buildClassNameSet();
        new ResuleMapBuilder().build(set, classLoader);
        new DaoBuilder().buildDao(set, classLoader);
        createMappers(set);
        createOrUpdateDatabase();
    }
    
    private Set<String> buildClassNameSet()
    {
        Set<String> set = new HashSet<String>();
        String[] packageNames = scanPackage.split(";");
        for (String packageName : packageNames)
        {
            for (String each : PackageScan.scan(packageName))
            {
                set.add(each);
            }
        }
        return set;
    }
    
    private void createMappers(Set<String> set)
    {
        try
        {
            MapperBuilder mapperBuilder = new MapperBuilder();
            mapperBuilder.initMetas(scanPackage);
            next: for (String each : set)
            {
                Class<?> ckass = classLoader == null ? Class.forName(each) : classLoader.loadClass(each);
                if (ckass.isInterface())
                {
                    for (Method method : ckass.getMethods())
                    {
                        if (method.isAnnotationPresent(Query.class) || method.isAnnotationPresent(Update.class) || method.isAnnotationPresent(BatchUpdate.class))
                        {
                            mappers.put(ckass, (Mapper) mapperBuilder.build(ckass).newInstance());
                            continue next;
                        }
                    }
                }
            }
            for (Mapper each : mappers.values())
            {
                each.setSessionFactory(this);
            }
        }
        catch (Exception e1)
        {
            throw new JustThrowException(e1);
        }
    }
    
    private void createOrUpdateDatabase()
    {
        if ("create".equals(tableMode) == false && "update".equals(tableMode) == false)
        {
            return;
        }
        int type = "create".equals(tableMode) ? 0 : 1;
        Structure structure;
        Verify.notNull(dbType, "dbType不能为空，必须指定内容。当前支持：mysql,MariaDB");
        if (dbType.equals("mysql"))
        {
            structure = new MariaDBStructure();
        }
        else if (dbType.equals("MariaDB"))
        {
            structure = new MariaDBStructure();
        }
        else
        {
            throw new RuntimeException("暂不支持的数据库结构类型新建或者修改,当前支持：mysql,MariaDB");
        }
        Connection connection = null;
        try
        {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            for (Dao<?> each : daos.values())
            {
                if (type == 0)
                {
                    structure.createTable(connection, each);
                }
                else
                {
                    structure.updateTable(connection, each);
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @Override
    public SqlSession getCurrentSession()
    {
        return sessionLocal.get();
    }
    
    @Override
    public SqlSession openSession()
    {
        try
        {
            SqlSession session = new SqlSessionImpl(dataSource.getConnection(), this);
            return session;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void removeCurrentSession()
    {
        sessionLocal.remove();
    }
    
    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
    
    @Override
    public void setCurrentSession(SqlSession session)
    {
        sessionLocal.set(session);
    }
    
    public void setScanPackage(String scanPackage)
    {
        this.scanPackage = scanPackage;
    }
    
    public void setTableMode(String mode)
    {
        tableMode = mode;
    }
    
    public void setDbType(String dbType)
    {
        this.dbType = dbType;
    }
    
    @Override
    public SqlSession getOrCreateCurrentSession()
    {
        SqlSession session = getCurrentSession();
        if (session == null)
        {
            session = openSession();
            sessionLocal.set(session);
        }
        return session;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMapper(Class<T> entityClass)
    {
        try
        {
            return (T) mappers.get(entityClass);
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
    class DaoBuilder
    {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void buildDao(Set<String> set, ClassLoader classLoader)
        {
            for (String each : set)
            {
                try
                {
                    Class<?> ckass;
                    if (classLoader == null)
                    {
                        ckass = Class.forName(each);
                    }
                    else
                    {
                        ckass = classLoader.loadClass(each);
                    }
                    if (ckass.isAnnotationPresent(TableEntity.class))
                    {
                        if (hasIdField(ckass))
                        {
                            daos.put(ckass, new DAOBeanImpl(ckass));
                        }
                    }
                }
                catch (ClassNotFoundException e)
                {
                    throw new RuntimeException(e);
                }
            }
            
        }
        
        private boolean hasIdField(Class<?> ckass)
        {
            Field[] fields = ReflectUtil.getAllFields(ckass);
            for (Field each : fields)
            {
                if (each.isAnnotationPresent(Id.class))
                {
                    return true;
                }
            }
            return false;
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> Dao<T> getDao(Class<T> ckass)
    {
        return (Dao<T>) daos.get(ckass);
    }
    
    class ResuleMapBuilder
    {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void build(Set<String> set, ClassLoader classLoader)
        {
            for (String each : set)
            {
                Class<?> ckass;
                try
                {
                    if (classLoader == null)
                    {
                        ckass = Class.forName(each);
                    }
                    else
                    {
                        ckass = classLoader.loadClass(each);
                    }
                    if (ckass.isAnnotationPresent(TableEntity.class))
                    {
                        ResultMap<?> resultMap = new ResultMapImpl(ckass);
                        resultMaps.put(ckass, resultMap);
                    }
                }
                catch (ClassNotFoundException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> ResultMap<T> getResultMap(Class<T> ckass)
    {
        return (ResultMap<T>) resultMaps.get(ckass);
    }
    
}
