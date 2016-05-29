package com.jfireframework.sql.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import com.jfireframework.baseutil.time.ThreadTimewatch;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.sql.function.LockMode;
import com.jfireframework.sql.test.entity.User;
import com.jfireframework.sql.test.entity.User2;
import com.jfireframework.sql.test.entity.User4;
import com.jfireframework.sql.test.table.User3;

public class DaoTest extends BaseTestSupport
{
    
    @Test
    public void saveTest()
    {
        User user = new User();
        user.setName("新的林斌");
        user.setPassword("weadasda");
        user.setAge(15);
        user.setBirthday("2015-5-6 12:12:12");
        ThreadTimewatch.start();
        for (int i = 0; i < 1000; i++)
        {
            session.save(user);
            user.setId(null);
        }
        ThreadTimewatch.end();
        logger.debug("在没有事务的情况下插入1000条数据耗时：{}", ThreadTimewatch.getTotalTime());
        ThreadTimewatch.start();
        session.beginTransAction();
        for (int i = 0; i < 1000; i++)
        {
            session.save(user);
            user.setId(null);
        }
        session.commit();
        ThreadTimewatch.end();
        logger.debug("在开启事务的情况下插入1000条数据耗时：{}", ThreadTimewatch.getTotalTime());
        List<User> users = new LinkedList<User>();
        for (int i = 0; i < 30000; i++)
        {
            user = new User();
            user.setName("新的林斌");
            user.setPassword("weadasda");
            user.setAge(15);
            user.setBirthday("2015-5-6 12:12:12");
            users.add(user);
        }
        ThreadTimewatch.start();
        session.batchInsert(users);
        ThreadTimewatch.end();
        logger.debug("在没有开启事务的情况下批量插入30000条数据耗时：{}", ThreadTimewatch.getTotalTime());
        ThreadTimewatch.start();
        session.beginTransAction();
        session.batchInsert(users);
        session.commit();
        ThreadTimewatch.end();
        logger.debug("在开启事务的情况下批量插入30000条数据耗时：{}", ThreadTimewatch.getTotalTime());
        
        try
        {
            PreparedStatement pstat = connection.prepareStatement("select username from user where password='weadasda'");
            ResultSet resultSet = pstat.executeQuery();
            resultSet.next();
            String username = resultSet.getString(1);
            assertEquals("新的林斌", username);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void updateTest()
    {
        User user = new User();
        user.setId(1);
        user.setName("新的林斌");
        user.setPassword("weadasda");
        user.setAge(15);
        user.setBirthday("2015-05-06 12:12:12");
        Timewatch timewatch = new Timewatch();
        timewatch.start();
        session.save(user);
        timewatch.end();
        logger.debug("更新花费的时间是{}", timewatch.getTotal());
        try
        {
            PreparedStatement pstat = connection.prepareStatement("select username from user where userid=1");
            ResultSet resultSet = pstat.executeQuery();
            resultSet.next();
            String username = resultSet.getString(1);
            assertEquals(user.getName(), username);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void deleteTest()
    {
        User user = new User();
        user.setId(1);
        session.delete(user);
        try
        {
            PreparedStatement pstat = connection.prepareStatement("select count(userid) from user");
            ResultSet resultSet = pstat.executeQuery();
            resultSet.next();
            assertEquals(2, resultSet.getInt(1));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void loadTest()
    {
        User user = session.get(User.class, 1);
        assertEquals("林斌", user.getName());
        assertEquals("2015-03-03 12:12:12.0", user.getBirthday());
    }
    
    @Test
    public void loadtest2()
    {
        User2 user = session.get(User2.class, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertEquals("2015-03-03 12:12:12", dateFormat.format(user.getBirthday().getTime()));
    }
    
    @Test
    public void testSaveUser2()
    {
        User2 user2 = new User2();
        Date date = new Date();
        user2.setBirthday(date);
        user2.setId(10);
        session.insert(user2);
        user2 = session.get(User2.class, 10);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertEquals(dateFormat.format(date), dateFormat.format(user2.getBirthday()));
        System.out.println(dateFormat.format(date));
    }
    
    @Test
    public void test1()
    {
        testUnit.clearSchemaData();
        testUnit.importExcelFile("test1.xlsx");
        User3 user3 = session.get(User3.class, 3);
        assertNull(user3.getWboy());
        user3 = new User3();
        user3.setId(4);
        user3.setAge(12);
        user3.setBirthday("2015-11-15");
        user3.setName("林斌");
        user3.setBoy(true);
        session.insert(user3);
        User3 result = session.get(User3.class, 4);
        assertTrue(result.isBoy());
        assertTrue(result.getWboy());
        User4 user4 = session.get(User4.class, 1);
        assertEquals(75.23, user4.getWeight(), 0.00001);
        user4 = new User4();
        user4.setWeight(56.23f);
        session.save(user4);
    }
    
    @Test
    public void test2()
    {
        User user = new User();
        user.setId(1);
        user.setAge(20);
        session.selectUpdate(user, "age");
        user = session.get(User.class, 1, "age");
        assertEquals(20, user.getAge().intValue());
        assertNull(user.getName());
        user = new User();
        user.setId(1);
        user.setAge(30);
        session.selectUpdate(user, "age");
        user = session.get(User.class, 1, "age");
        assertEquals(30, user.getAge().intValue());
        assertNull(user.getName());
    }
    
    @Test
    public void test3()
    {
        User user = session.get(User.class, 1, LockMode.SHARE);
        assertEquals("林斌", user.getName());
    }
}
