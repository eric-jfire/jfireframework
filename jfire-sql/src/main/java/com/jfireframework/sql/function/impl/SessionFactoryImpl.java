package com.jfireframework.sql.function.impl;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import com.jfireframework.baseutil.PackageScan;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.context.bean.annotation.field.CanBeNull;
import com.jfireframework.sql.annotation.Query;
import com.jfireframework.sql.annotation.Update;
import com.jfireframework.sql.extra.dbstructure.DefaultNameStrategy;
import com.jfireframework.sql.extra.dbstructure.MariaDBStructure;
import com.jfireframework.sql.extra.dbstructure.NameStrategy;
import com.jfireframework.sql.extra.dbstructure.Structure;
import com.jfireframework.sql.extra.interceptor.SqlInterceptor;
import com.jfireframework.sql.extra.interceptor.SqlPreInterceptor;
import com.jfireframework.sql.function.Dao;
import com.jfireframework.sql.function.SessionFactory;
import com.jfireframework.sql.function.SqlSession;
import com.jfireframework.sql.function.mapper.Mapper;
import com.jfireframework.sql.metadata.MetaContext;
import com.jfireframework.sql.metadata.TableMetaData;
import com.jfireframework.sql.page.MysqlParse;
import com.jfireframework.sql.page.PageParse;
import com.jfireframework.sql.resultsettransfer.TransferContext;
import com.jfireframework.sql.util.MapperBuilder;

public class SessionFactoryImpl implements SessionFactory
{
    @Resource
    protected DataSource                        dataSource;
    @Resource
    @CanBeNull
    protected ClassLoader                       classLoader;
    protected static ThreadLocal<SqlSession>    sessionLocal     = new ThreadLocal<SqlSession>();
    protected String                            scanPackage;
    // 如果值是create，则会创建表。
    protected String                            tableMode        = "none";
    protected IdentityHashMap<Class<?>, Mapper> mappers          = new IdentityHashMap<Class<?>, Mapper>(128);
    protected IdentityHashMap<Class<?>, Dao<?>> daos             = new IdentityHashMap<Class<?>, Dao<?>>();
    public static NameStrategy                  nameStrategy     = new DefaultNameStrategy();
    protected MetaContext                       metaContext;
    protected TransferContext                   transferContext  = new TransferContext();
    protected boolean                           resultFieldCache = true;
    protected SqlPreInterceptor[]               preInterceptors;
    protected SqlInterceptor[]                  sqlInterceptors;
    protected PageParse                         pageParse;
    protected String                            productName;
    protected static final Logger               logger           = ConsoleLogFactory.getLogger();
    
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
            preInterceptors = findSqlPreInterceptors(set);
            sqlInterceptors = findSqlInterceptor(set);
            pageParse = findPageParse();
            metaContext = new MetaContext(set);
            createOrUpdateDatabase();
            createMappers(set);
            new DaoBuilder().buildDao();
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
    private SqlPreInterceptor[] findSqlPreInterceptors(Set<Class<?>> set) throws InstantiationException, IllegalAccessException
    {
        List<SqlPreInterceptor> list = new LinkedList<SqlPreInterceptor>();
        for (Class<?> each : set)
        {
            if (SqlPreInterceptor.class.isAssignableFrom(each))
            {
                list.add((SqlPreInterceptor) each.newInstance());
            }
        }
        return list.toArray(new SqlPreInterceptor[list.size()]);
    }
    
    private SqlInterceptor[] findSqlInterceptor(Set<Class<?>> set) throws InstantiationException, IllegalAccessException
    {
        List<SqlInterceptor> list = new LinkedList<SqlInterceptor>();
        for (Class<?> each : set)
        {
            if (SqlInterceptor.class.isAssignableFrom(each))
            {
                list.add((SqlInterceptor) each.newInstance());
            }
        }
        return list.toArray(new SqlInterceptor[list.size()]);
    }
    
    public PageParse findPageParse()
    {
        Connection connection = null;
        try
        {
            connection = dataSource.getConnection();
            DatabaseMetaData md = connection.getMetaData();
            productName = md.getDatabaseProductName().toLowerCase();
            if (productName.equals("mariadb") || "mysql".equals(productName))
            {
                return new MysqlParse();
            }
            else
            {
                logger.error("不支持分页的数据库类型：{}", productName);
                return null;
            }
        }
        catch (SQLException e)
        {
            throw new JustThrowException(e);
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
            
            MapperBuilder mapperBuilder = new MapperBuilder(metaContext, transferContext, resultFieldCache);
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
    
    private Structure buildStructure()
    {
        if (productName.equals("mysql"))
        {
            return new MariaDBStructure();
        }
        else if (productName.equals("mariadb"))
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
    
    private void createOrUpdateDatabase() throws Exception
    {
        TableMode type = TableMode.valueOf(tableMode);
        switch (type)
        {
            case none:
                return;
            case create:
            {
                Structure structure = buildStructure();
                structure.createTable(dataSource, metaContext.metaDatas());
                return;
            }
            case update:
            {
                Structure structure = buildStructure();
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
            SqlSession session = new SqlSessionImpl(dataSource.getConnection(), this, preInterceptors, sqlInterceptors, pageParse, transferContext);
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
        public void buildDao()
        {
            for (TableMetaData each : metaContext.metaDatas())
            {
                if (each.getIdInfo() != null)
                {
                    daos.put(each.getEntityClass(), new DAOBeanImpl(each, preInterceptors));
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
    
}
