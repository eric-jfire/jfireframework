package com.jfireframework.sql.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.jfireframework.sql.page.MysqlPage;
import com.jfireframework.sql.test.entity.User;

public class InterfaceTest extends BaseTestSupport
{
    @Test
    public void getUserByIdTest()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        User user = null;
        user = userDAO.getUserByid(1);
        assertEquals(1, user.getId().intValue());
        assertEquals("林斌", user.getName());
        assertEquals(15, user.getAge().intValue());
    }
    
    @Test
    public void getUserAgeTest()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        System.out.println(userDAO.getUserAge(1));
    }
    
    @Test
    public void getUserByIdWithNameTest()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        List<User> users = userDAO.getUserByidWithName(1);
        User user = users.get(0);
        assertEquals(1, user.getId().intValue());
        assertEquals("林斌", user.getName());
        assertEquals(15, user.getAge().intValue());
    }
    
    @Test
    public void insertUserTest()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        User user = new User();
        user.setId(4);
        user.setPassword("123132");
        user.setName("212");
        user.setAge(15);
        user.setBirthday("2015-3-6 12:10:10");
        logger.debug("插入的用户条数是{}", userDAO.insertUser(user));
        try
        {
            PreparedStatement pstat = session.getConnection().prepareStatement("select * from user where userid=4");
            ResultSet resultSet = pstat.executeQuery();
            resultSet.next();
            assertEquals(user.getName(), resultSet.getString("username"));
            assertEquals(user.getAge().intValue(), resultSet.getInt("age"));
            assertEquals("2015-03-06 12:10:10.0", resultSet.getString("birthday"));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void getUsernameTest()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        userDAO.getUserName(1);
        String username = userDAO.getUserName(1);
        assertEquals("林斌", username);
    }
    
    @Test
    public void deleteUserTest()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        logger.debug("删除的用户条数是{}", userDAO.deleteUser(1));
        try
        {
            PreparedStatement pstat = session.getConnection().prepareStatement("select count(userid) from user where userid=1");
            ResultSet resultSet = pstat.executeQuery();
            resultSet.next();
            assertEquals(0, resultSet.getInt(1));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void getUsernamesTest()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        List<String> list = userDAO.getUsernames();
        assertEquals("林斌", list.get(0));
        assertEquals("林曦", list.get(1));
    }
    
    @Test
    public void getUsernamesTest2()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        List<String> list = userDAO.getUsernames2(1);
        assertEquals("林斌", list.get(0));
    }
    
    @Test
    public void dyncTest()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        User user = new User();
        user.setName("林斌");
        List<User> list = userDAO.dynamicQuery(user);
        assertEquals(1, list.size());
    }
    
    @Test
    public void dyncTest2()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        User user = new User();
        user.setName("林斌");
        MysqlPage page = new MysqlPage();
        page.setPage(1);
        page.setPageSize(20);
        List<User> list = userDAO.dynamicQuery2(user, page);
        assertEquals(1, page.getTotal());
    }
    
    @Test
    public void testquestion()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        List<User> list = userDAO.listinquestion("2,1,");
        assertEquals("林斌", list.get(0).getName());
        assertEquals(1, list.size());
    }
    
    @Test
    public void testquestion2()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        List<User> list = userDAO.listinquestion(new int[] { 2, 1 });
        assertEquals("林斌", list.get(0).getName());
        assertEquals(1, list.size());
    }
    
    @Test
    public void testquestion3()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        List<User> list = userDAO.listinquestion(new Integer[] { 2, 1 });
        assertEquals("林斌", list.get(0).getName());
        assertEquals(1, list.size());
    }
    
    @Test
    public void testupdatename()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        userDAO.updatename("li", "2,3");
        assertEquals("li", userDAO.getUserName(2));
    }
    
    @Test
    public void name2Test()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        System.out.println(userDAO.name2(14));
    }
    
    @Test
    public void selectTest()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        System.out.println(userDAO.select(0));
    }
    
    @Test
    public void name3Test()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        assertEquals("林斌", userDAO.name3("user"));
    }
    
    @Test
    public void test4()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        MysqlPage page = new MysqlPage();
        page.setPage(1);
        page.setPageSize(20);
        userDAO.getUsernames(page);
        session.close();
        assertEquals(3, page.getTotal());
    }
    
    @Test
    public void test5()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(1);
        userDAO.querySize(ids);
    }
    
    @Test
    public void test6()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        userDAO.functionUse("assd");
    }
    
    @Test
    public void test7()
    {
        UserDAO userDAO = sessionFactory.getMapper(UserDAO.class);
        userDAO.functionUse2("asdasd", "dsad");
    }
}
