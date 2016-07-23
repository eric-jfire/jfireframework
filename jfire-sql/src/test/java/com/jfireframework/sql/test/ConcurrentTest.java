package com.jfireframework.sql.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Ignore;
import org.junit.Test;
import com.jfireframework.sql.function.SqlSession;
import com.jfireframework.sql.test.entity.User;

public class ConcurrentTest extends BaseTestSupport
{
    
    @Test
    public void test() throws InterruptedException
    {
        final UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < 100; i++)
        {
            pool.submit(
                    new Runnable() {
                        
                        @Override
                        public void run()
                        {
                            User user = userDAO.getUserByid(1);
                            logger.debug("userid:{},name:{},age:{}", user.getId(), user.getName(), user.getAge());
                        }
                    }
            );
        }
        pool.shutdown();
        pool.awaitTermination(1000, TimeUnit.SECONDS);
    }
    
    @Test
    @Ignore
    public void test2() throws InterruptedException
    {
        new Thread(
                new Runnable() {
                    
                    @Override
                    public void run()
                    {
                        try
                        {
                            SqlSession session1 = sessionFactory.getOrCreateCurrentSession();
                            session1.beginTransAction();
                            logger.debug("线程1事务开始");
                            User user = session1.get(User.class, 1);
                            logger.debug("读取数据");
                            user.setAge(user.getAge() - 2);
                            logger.debug("更新数据，年龄是{}", user.getAge());
                            session1.save(user);
                            logger.debug("保存数据");
                            logger.debug("进入等待");
                            Thread.sleep(800);
                            user = session1.get(User.class, 1);
                            logger.debug("没提交前再次查询，年龄是{}", user.getAge());
                            logger.debug("提交事务");
                            session1.commit();
                            user = session1.get(User.class, 1);
                            logger.debug("提交后查询{}", user.getAge());
                            logger.debug("提交事务完成");
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
        new Thread(
                new Runnable() {
                    
                    @Override
                    public void run()
                    {
                        SqlSession session2 = sessionFactory.getOrCreateCurrentSession();
                        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
                        try
                        {
                            Thread.sleep(500);
                            session2.beginTransAction();
                            logger.debug("线程2事务开始");
                            User user = session2.get(User.class, 1);
                            logger.debug("读取数据，年龄是{}", user.getAge());
                            Thread.sleep(500);
                            user = session2.get(User.class, 1);
                            logger.debug("再次查询的年龄是{}", user.getAge());
                            user = userDAO.selectForUpdate(1);
                            
                            logger.debug("第三次查询的年龄是{}", user.getAge());
                            user = session2.get(User.class, 1);
                            
                            logger.debug("第四次查询的年龄是{}", user.getAge());
                            logger.debug("保存数据");
                            logger.debug("事务提交");
                            session2.commit();
                            logger.debug("事务提交完成");
                        }
                        catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        
                    }
                }
        ).start();
        Thread.sleep(5000);
        logger.debug("最后的年龄是{}", session.get(User.class, 1).getAge());
    }
}
