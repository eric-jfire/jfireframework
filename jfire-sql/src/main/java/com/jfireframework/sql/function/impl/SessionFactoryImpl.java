package com.jfireframework.sql.function.impl;

import java.lang.reflect.Method;
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
import com.jfireframework.context.bean.annotation.field.CanBeNull;
import com.jfireframework.sql.annotation.Query;
import com.jfireframework.sql.annotation.Update;
import com.jfireframework.sql.dbstructure.DefaultNameStrategy;
import com.jfireframework.sql.dbstructure.MariaDBStructure;
import com.jfireframework.sql.dbstructure.NameStrategy;
import com.jfireframework.sql.dbstructure.Structure;
import com.jfireframework.sql.function.Dao;
import com.jfireframework.sql.function.ResultMap;
import com.jfireframework.sql.function.SessionFactory;
import com.jfireframework.sql.function.SqlSession;
import com.jfireframework.sql.function.mapper.Mapper;
import com.jfireframework.sql.metadata.MetaContext;
import com.jfireframework.sql.metadata.TableMetaData;
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
    public static NameStrategy                  nameStrategy = new DefaultNameStrategy();
    protected MetaContext                       metaContext;
    
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
        try
        {
            if (dataSource == null)
            {
                return;
            }
            if (classLoader == null)
            {
                classLoader = Thread.currentThread().getContextClassLoader();
            }
            Set<Class<?>> set = buildClassNameSet(classLoader);
            metaContext = new MetaContext(set);
            createOrUpdateDatabase(dataSource);
            createMappers(set);
            new DaoBuilder().buildDao(set);
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
    private Set<Class<?>> buildClassNameSet(ClassLoader classLoader) throws ClassNotFoundException
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
        Set<Class<?>> types = new HashSet<Class<?>>();
        for (String each : set)
        {
            types.add(classLoader.loadClass(each));
        }
        return types;
    }
    
    private void createMappers(Set<Class<?>> set)
    {
        try
        {
            MapperBuilder mapperBuilder = new MapperBuilder(metaContext);
            nextSqlInterface: for (Class<?> each : set)
            {
                if (each.isInterface())
                {
                    for (Method method : each.getMethods())
                    {
                        if (method.isAnnotationPresent(Query.class) || method.isAnnotationPresent(Update.class))
                        {
                            mappers.put(each, (Mapper) mapperBuilder.build(each).newInstance());
                            continue nextSqlInterface;
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
    
    private Structure buildStructure(String dbType)
    {
        if (dbType.equals("mysql"))
        {
            return new MariaDBStructure();
        }
        else if (dbType.equals("MariaDB"))
        {
            return new MariaDBStructure();
        }
        else
        {
            throw new IllegalArgumentException("暂不支持的数据库结构类型新建或者修改,当前支持：mysql,MariaDB");
        }
    }
    
    enum TableMode
    {
        create, update, none
    }
    
    private void createOrUpdateDatabase(DataSource dataSource) throws Exception
    {
        TableMode type = TableMode.valueOf(tableMode);
        switch (type)
        {
            case none:
                return;
            case create:
            {
                Structure structure = buildStructure(dbType);
                structure.createTable(dataSource, metaContext.metaDatas());
                return;
            }
            case update:
            {
                Structure structure = buildStructure(dbType);
                structure.updateTable(dataSource, metaContext.metaDatas());
                return;
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
        @SuppressWarnings("rawtypes")
        public void buildDao(Set<Class<?>> set)
        {
            for (TableMetaData each : metaContext.metaDatas())
            {
                if (each.getIdInfo() != null)
                {
                    daos.put(each.getEntityClass(), new DAOBeanImpl(each));
                }
            }
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> Dao<T> getDao(Class<T> ckass)
    {
        return (Dao<T>) daos.get(ckass);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> ResultMap<T> getResultMap(Class<T> ckass)
    {
        return (ResultMap<T>) resultMaps.get(ckass);
    }
    
}
