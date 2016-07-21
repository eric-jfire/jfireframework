package com.jfireframework.sql.test;

import javax.annotation.Resource;
import com.jfireframework.context.aop.annotation.Transaction;
import com.jfireframework.sql.function.SessionFactory;
import com.jfireframework.sql.function.SqlSession;

@Resource
public class UserService
{
    @Resource
    private SessionFactory sessionFactory;
    
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }
    
    @Transaction
    public void userop()
    {
        System.out.println("caozuo");
        SqlSession session = sessionFactory.getCurrentSession();
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        op2();
        System.out.println("操作完毕");
        userDAO.deleteUser(1);
    }
    
    @Transaction
    public void op2()
    {
        SqlSession session = sessionFactory.getCurrentSession();
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        userDAO.getUserByidWithName(1);
    }
}
