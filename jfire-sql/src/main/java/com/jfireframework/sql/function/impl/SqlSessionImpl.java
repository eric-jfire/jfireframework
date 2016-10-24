package com.jfireframework.sql.function.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.sql.function.LockMode;
import com.jfireframework.sql.function.SessionFactory;
import com.jfireframework.sql.function.SqlSession;

public class SqlSessionImpl implements SqlSession
{
    private int            transNum = 0;
    private Connection     connection;
    private SessionFactory sessionFactory;
    private static Logger  logger   = ConsoleLogFactory.getLogger();
    private boolean        closed   = false;
    
    public SqlSessionImpl(Connection connection, SessionFactory sessionFactory)
    {
        logger.trace("打开sqlsession");
        this.connection = connection;
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public void beginTransAction(int isolate)
    {
        try
        {
            if (transNum == 0)
            {
                transNum++;
                connection.setAutoCommit(false);
                if (isolate > 0)
                {
                    connection.setTransactionIsolation(isolate);
                }
            }
            else
            {
                transNum++;
            }
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
            if (transNum == 0)
            {
                connection.commit();
            }
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
            if (transNum == 0)
            {
                connection.rollback();
            }
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
            sessionFactory.removeCurrentSession();
            connection.close();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("关闭", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> int delete(T entity)
    {
        return sessionFactory.getDao((Class<T>) entity.getClass()).delete(entity, connection);
    }
    
    @Override
    public <T> T get(Class<T> entityClass, Object pk)
    {
        return sessionFactory.getDao(entityClass).getById(pk, connection);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> void save(T entity)
    {
        sessionFactory.getDao((Class<T>) entity.getClass()).save(entity, connection);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> void batchInsert(List<T> entitys)
    {
        sessionFactory.getDao((Class<T>) entitys.get(0).getClass()).batchInsert(entitys, connection);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> void insert(T entity)
    {
        sessionFactory.getDao((Class<T>) entity.getClass()).insert(entity, connection);
    }
    
    @Override
    public Connection getConnection()
    {
        return connection;
    }
    
    @Override
    public <T> T get(Class<T> entityClass, Object pk, LockMode mode)
    {
        return sessionFactory.getDao(entityClass).getById(pk, connection, mode);
    }
    
    @Override
    public int update(String sql)
    {
        PreparedStatement pStat = null;
        try
        {
            pStat = connection.prepareStatement(sql);
            return pStat.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            if (pStat != null)
            {
                try
                {
                    pStat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @Override
    public <T> T findBy(Class<T> entityClass, String name, Object param)
    {
        return sessionFactory.getDao(entityClass).findBy(name, param, connection);
    }
    
}
