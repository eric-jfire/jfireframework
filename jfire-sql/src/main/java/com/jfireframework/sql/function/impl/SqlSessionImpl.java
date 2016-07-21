package com.jfireframework.sql.function.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.sql.function.LockMode;
import com.jfireframework.sql.function.MapBean;
import com.jfireframework.sql.function.SessionFactory;
import com.jfireframework.sql.function.SqlSession;
import com.jfireframework.sql.util.DaoFactory;
import com.jfireframework.sql.util.MapBeanFactory;
import com.jfireframework.sql.util.SqlTool;

public class SqlSessionImpl implements SqlSession
{
    private volatile int         transNum     = 0;
    private Connection           connection;
    private SessionFactory       sessionFactory;
    private static Logger        logger       = ConsoleLogFactory.getLogger();
    private volatile boolean     closed       = false;
    private static Set<Class<?>> baseClassSet = new HashSet<Class<?>>();
    private long                 t0           = System.currentTimeMillis();
    
    static
    {
        baseClassSet.add(String.class);
        baseClassSet.add(Integer.class);
        baseClassSet.add(Long.class);
        baseClassSet.add(Float.class);
        baseClassSet.add(Short.class);
        baseClassSet.add(Double.class);
        baseClassSet.add(Boolean.class);
        baseClassSet.add(Byte.class);
        baseClassSet.add(int.class);
        baseClassSet.add(long.class);
        baseClassSet.add(float.class);
        baseClassSet.add(short.class);
        baseClassSet.add(double.class);
        baseClassSet.add(boolean.class);
        baseClassSet.add(char.class);
        baseClassSet.add(byte.class);
    }
    
    public SqlSessionImpl(Connection connection, SessionFactory sessionFactory)
    {
        logger.trace("打开sqlsession");
        this.connection = connection;
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public void beginTransAction()
    {
        try
        {
            transNum++;
            connection.setAutoCommit(false);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void commit()
    {
        try
        {
            transNum--;
            connection.commit();
        }
        catch (SQLException e)
        {
            logger.error("事务提交出现异常，请确认当前连接是否仍然还在事务内。请不要在一个事务内开启两个连接");
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void flush()
    {
        try
        {
            connection.commit();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void rollback()
    {
        try
        {
            transNum--;
            connection.rollback();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void close()
    {
        if (closed || transNum > 0)
        {
            return;
        }
        try
        {
            closed = true;
            connection.setAutoCommit(true);
            sessionFactory.removeCurrentSession();
            connection.close();
            logger.trace("sqlsession关闭,共使用{}毫秒", (System.currentTimeMillis() - t0));
        }
        catch (SQLException e)
        {
            throw new RuntimeException("关闭", e);
        }
    }
    
    @Override
    public boolean delete(Object entity)
    {
        return DaoFactory.getDaoBean(entity.getClass()).delete(entity, connection);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> entityClass, Object pk)
    {
        return (T) DaoFactory.getDaoBean(entityClass).getById(pk, connection);
    }
    
    @Override
    public <T> void save(T entity)
    {
        DaoFactory.getDaoBean(entity.getClass()).save(entity, connection);
    }
    
    @Override
    public <T> void batchInsert(List<T> entitys)
    {
        DaoFactory.getDaoBean(entitys.get(0).getClass()).batchInsert(entitys, connection);
    }
    
    public void insert(Object entity)
    {
        DaoFactory.getDaoBean(entity.getClass()).insert(entity, connection);
    }
    
    @Override
    public int update(String sql, Object... params)
    {
        logger.trace("查询使用的sql是：{}", sql);
        PreparedStatement pstat = null;
        try
        {
            pstat = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++)
            {
                pstat.setObject(i + 1, params[i]);
            }
            return pstat.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (pstat != null)
            {
                try
                {
                    pstat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @Override
    public int[] batchUpdate(String sql, List<Object[]> list)
    {
        logger.trace("使用的sql是{}", sql);
        PreparedStatement pstat = null;
        try
        {
            pstat = connection.prepareStatement(sql);
            for (Object[] each : list)
            {
                int length = each.length;
                for (int i = 0; i < length; i++)
                {
                    pstat.setObject(i + 1, each[i]);
                }
                pstat.addBatch();
            }
            return pstat.executeBatch();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (pstat != null)
            {
                try
                {
                    pstat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @Override
    public Connection getConnection()
    {
        return connection;
    }
    
    public int getTransNum()
    {
        return transNum;
    }
    
    @Override
    public List<Object[]> listQuery(Class<?>[] resultTypes, String sql, Object... params)
    {
        PreparedStatement pstat = null;
        try
        {
            pstat = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++)
            {
                pstat.setObject(i + 1, params[i]);
            }
            ResultSet resultSet = pstat.executeQuery();
            List<Object[]> list = new ArrayList<Object[]>();
            int colNum = resultSet.getMetaData().getColumnCount();
            Object[] tmp;
            while (resultSet.next())
            {
                tmp = new Object[colNum];
                for (int i = 0; i < colNum; i++)
                {
                    tmp[i] = SqlTool.getValue(resultSet, i + 1, resultTypes[i]);
                }
                list.add(tmp);
            }
            return list;
        }
        catch (SQLException e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            if (pstat != null)
            {
                try
                {
                    pstat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @Override
    public <T> List<T> listQuery(Class<T> resultType, String sql, Object... params)
    {
        logger.trace("查询使用的sql是：{}", sql);
        PreparedStatement pstat = null;
        try
        {
            pstat = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++)
            {
                pstat.setObject(i + 1, params[i]);
            }
            ResultSet resultSet = pstat.executeQuery();
            MapBean<T> mapBean = MapBeanFactory.getBean(resultType);
            return mapBean.toBean(resultSet);
        }
        catch (Exception e)
        {
            StringCache cache = new StringCache("查询出错，sql语句是:");
            cache.append(sql).append(",查询的条件是");
            for (int i = 0; i < params.length; i++)
            {
                cache.append(params[i]).append(',');
            }
            throw new RuntimeException(cache.toString(), e);
        }
        finally
        {
            if (pstat != null)
            {
                try
                {
                    pstat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> baseListQuery(Class<T> resultType, String sql, Object... params)
    {
        logger.trace("查询使用的sql是：{}", sql);
        Verify.True(baseClassSet.contains(resultType), "该方法的查询入参中，类型只能是基本类型或者其包装类");
        PreparedStatement pstat = null;
        try
        {
            pstat = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++)
            {
                pstat.setObject(i + 1, params[i]);
            }
            ResultSet resultSet = pstat.executeQuery();
            List<Object> list = new ArrayList<Object>();
            int colNum = resultSet.getMetaData().getColumnCount();
            Verify.True(colNum == 1, "查询sql：{} 返回的结果数量不是1", sql);
            while (resultSet.next())
            {
                list.add(SqlTool.getValue(resultSet, 1, resultType));
            }
            return (List<T>) list;
        }
        catch (SQLException e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            if (pstat != null)
            {
                try
                {
                    pstat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T baseQuery(Class<T> resultType, String sql, Object... params)
    {
        logger.trace("查询使用的sql是：{}", sql);
        Verify.True(baseClassSet.contains(resultType), "该方法的查询入参中，类型只能是基本类型或者其包装类");
        PreparedStatement pstat = null;
        try
        {
            pstat = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++)
            {
                pstat.setObject(i + 1, params[i]);
            }
            ResultSet resultSet = pstat.executeQuery();
            int colNum = resultSet.getMetaData().getColumnCount();
            Verify.True(colNum == 1, "sql:{}查询因为是基本类型，要求返回结果只能是单行单列", sql);
            int num = 0;
            T result = null;
            while (resultSet.next())
            {
                result = (T) SqlTool.getValue(resultSet, 1, resultType);
                num++;
                if (num > 1)
                {
                    throw new RuntimeException("查询结果不是唯一的,请检查");
                }
            }
            return result;
        }
        catch (SQLException e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            if (pstat != null)
            {
                try
                {
                    pstat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @Override
    public <T> T query(Class<T> resultType, String sql, Object... params)
    {
        logger.trace("查询使用的sql是：{}", sql);
        PreparedStatement pstat = null;
        try
        {
            pstat = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++)
            {
                pstat.setObject(i + 1, params[i]);
            }
            ResultSet resultSet = pstat.executeQuery();
            MapBean<T> mapBean = MapBeanFactory.getBean(resultType);
            return mapBean.singleResultToBean(resultSet);
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            if (pstat != null)
            {
                try
                {
                    pstat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> entityClass, Object pk, LockMode mode)
    {
        return (T) DaoFactory.getDaoBean(entityClass).getById(pk, connection, mode);
    }
    
    @Override
    public int selectUpdate(Object entity, String fieldNames)
    {
        return DaoFactory.getDaoBean(entity.getClass()).update(entity, connection, fieldNames);
    }
    
    @Override
    public <T> T get(Class<T> entityClass, Object pk, String fieldNames)
    {
        return DaoFactory.getDaoBean(entityClass).getById(pk, connection, fieldNames);
    }
    
    @Override
    public int deleteByIds(Class<?> entityClass, String ids)
    {
        return DaoFactory.getDaoBean(entityClass).deleteByIds(ids, connection);
    }
    
    @Override
    public int deleteByIds(Class<?> entityClass, int[] ids)
    {
        return DaoFactory.getDaoBean(entityClass).deleteByIds(ids, connection);
    }
    
}
