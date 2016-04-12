package com.jfireframework.sql.test;

import static org.junit.Assert.assertEquals;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.sql.function.SqlSession;
import com.jfireframework.sql.test.entity.User;

public class SessionTest extends BaseTestSupport
{
    @Test
    public void sqlQueryTest()
    {
        logger.info("测试sqlQuery(String sql, Object... params)");
        String sql = "select userid,birthday,age from user where userid=?";
        List<Object[]> list = (List<Object[]>) session.listQuery(new Class<?>[] { Integer.class, String.class, Integer.class }, sql, 1);
        Object[] result = list.get(0);
        assertEquals(1, ((Integer) result[0]).intValue());
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 2, 3, 12, 12, 12);
        calendar.set(Calendar.MILLISECOND, 0);
        System.out.println(result[1]);
        assertEquals("2015-03-03 12:12:12.0", (String) result[1]);
        assertEquals(15, ((Integer) result[2]).intValue());
    }
    
    @Test
    public void sqlQueryTest5()
    {
        logger.info("测试sqlQueryByName(Class<User> t, String sql, Object... params)");
        String sql = "select * from user where userid=?";
        List<User> users = (List<User>) session.listQuery(User.class, sql, 1);
        User user = users.get(0);
        assertEquals(15, user.getAge().intValue());
        assertEquals("林斌", user.getName());
        assertEquals("2015-03-03 12:12:12.0", user.getBirthday());
    }
    
    @Test
    public void sqlQueryTest6()
    {
        String sql = "select * from user where userid=?";
        List<User> users = (List<User>) session.listQuery(User.class, sql, 1);
        User user = users.get(0);
        assertEquals(15, user.getAge().intValue());
        assertEquals("林斌", user.getName());
        assertEquals("2015-03-03 12:12:12.0", user.getBirthday());
    }
    
    @Test
    public void sqlUpdateTest()
    {
        logger.info("测试:{}", "sqlUpdate(String sql, Object... params)");
        String sql = "update user set username=?,birthday=? where userid=?";
        session.update(sql, "新的林斌", "2014-6-6 0:0:0", 1);
        try (PreparedStatement pstat = session.getConnection().prepareStatement("select * from user where userid=1"))
        {
            ResultSet resultSet = pstat.executeQuery();
            resultSet.next();
            assertEquals("新的林斌", resultSet.getString("username"));
            assertEquals("2014-06-06 00:00:00.0", resultSet.getString("birthday"));
        }
        catch (SQLException e)
        {
            Assert.fail();
        }
    }
    
    @Test
    public void batchSqlUpdateTest()
    {
        logger.info("测试{}", "batchSqlUpdate(String sql, Iterator<Object[]> iterator)");
        List<Object[]> list = new ArrayList<>();
        Object[] data = new Object[] { "林斌1", "2015-6-6 0:0:0", 13, 4, "dasds" };
        list.add(data);
        data = new Object[] { "林斌2", "2015-6-7 0:0:0", 15, 5, "dsads" };
        list.add(data);
        String sql = "insert into user (username,birthday,age,userid,password) values(?,?,?,?,?)";
        session.batchUpdate(sql, list);
        try (PreparedStatement pstat = session.getConnection().prepareStatement("select * from user where userid in(5,4) order by userid"))
        {
            ResultSet rs = pstat.executeQuery();
            rs.next();
            assertEquals("林斌1", rs.getString("username"));
            assertEquals("2015-06-06 00:00:00.0", rs.getString("birthday"));
            assertEquals(13, rs.getInt("age"));
            rs.next();
            assertEquals("林斌2", rs.getString("username"));
            assertEquals("2015-06-07 00:00:00.0", rs.getString("birthday"));
            assertEquals(15, rs.getInt("age"));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void transactionNullTest()
    {
        session.beginTransAction();
        session.update("insert into user (username,age,userid,password) values('你好',12,4,'sdads')");
        SqlSession tmp = sessionFactory.openSession();
        List<Object[]> tmpResult = (List<Object[]>) tmp.listQuery(new Class<?>[] { String.class, Integer.class }, "select username,age from  user where userid=4");
        assertEquals(0, tmpResult.size());
        tmp.close();
        session.rollback();
    }
    
    @Test
    public void transactionTest()
    {
        session.beginTransAction();
        session.update("insert into user (username,age,userid,password) values('你好',12,4,'dasda')");
        SqlSession tmp = sessionFactory.openSession();
        session.commit();
        List<Object[]> tmpResult = (List<Object[]>) tmp.listQuery(new Class<?>[] { String.class, Integer.class }, "select username,age from  user where userid=4");
        assertEquals(1, tmpResult.size());
        tmp.close();
    }
    
}
