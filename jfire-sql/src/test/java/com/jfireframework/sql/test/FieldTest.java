package com.jfireframework.sql.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import org.junit.Test;
import com.jfireframework.sql.test.entity.User4;
import com.jfireframework.sql.test.entity.User5;
import com.jfireframework.sql.test.entity.User6;
import com.jfireframework.sql.test.entity.User7;
import com.jfireframework.sql.test.entity.User8;

public class FieldTest extends BaseTestSupport
{
    @Test
    public void test()
    {
        User4 user4 = new User4();
        session.save(user4);
        testUnit.clearSchemaData();
        testUnit.importExcelFile("test1.xlsx");
        user4 = session.get(User4.class, 3);
        assertNull(user4.getDate());
    }
    
    @Test
    public void test2()
    {
        User5 user5 = session.get(User5.class, 1);
        System.out.println(user5.getDate());
        user5 = new User5();
        user5.setDate(new Timestamp(new Date().getTime()));
        user5.setBoy(true);
        session.save(user5);
        user5.setBoy(null);
        user5.setId(null);
        session.insert(user5);
    }
    
    @Test
    public void test3()
    {
        User6 user6 = new User6();
        user6.setTime(new Time(new Date().getTime()));
        session.save(user6);
    }
    
    @Test
    public void test4()
    {
        User7 user7 = session.get(User7.class, 1);
        assertEquals(15, user7.getAge());
        user7 = new User7();
        user7.setAge(16);
        session.save(user7);
    }
    
    @Test
    public void test5()
    {
        User8 user8 = session.get(User8.class, 1);
        assertEquals(15, user8.getAge());
        assertEquals(75.23f, user8.getWeight().floatValue(), 0.0001);
        user8 = session.get(User8.class, 2);
        assertNull(user8.getWeight());
        user8 = new User8();
        user8.setAge(16);
        user8.setWeight(12.36f);
        session.save(user8);
        user8.setWeight(null);
        user8.setUserid(null);
        session.save(user8);
    }
    
    @Test
    public void test6()
    {
        User7 user7 = session.get(User7.class, 1);
        assertEquals(75.23d, user7.getWeight().doubleValue(), 0.0001);
        user7 = session.get(User7.class, 2);
        assertNull(user7.getWeight());
        user7 = new User7();
        user7.setWeight(753.23);
        user7.setCalendar(Calendar.getInstance());
        session.save(user7);
        User4 user4 = new User4();
        user4.setAge(100l);
        session.save(user4);
        testUnit.clearSchemaData();
        testUnit.importExcelFile("test1.xlsx");
        session.get(User7.class, 3);
    }
    
}
