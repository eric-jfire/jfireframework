package com.jfireframework.baseutil;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import com.jfireframework.baseutil.uniqueid.Uid;
import com.jfireframework.baseutil.uniqueid.WinterId;

public class IdTest
{
    @Test
    public void test()
    {
        Uid uid = new WinterId((byte) 1);
        Set<String> set = new HashSet<String>(100000);
        for (int i = 0; i < 100000; i++)
        {
            if (set.add(uid.generateDigits()) == false)
            {
                throw new UnsupportedOperationException();
            }
        }
    }
    
    @Test
    public void test2()
    {
        Uid uid = new WinterId((byte) 1);
        for (int i = 0; i < 10; i++)
        {
            System.out.println(uid.generateDigits());
        }
    }
}
