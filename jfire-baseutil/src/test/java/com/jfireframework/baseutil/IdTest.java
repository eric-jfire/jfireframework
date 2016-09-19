package com.jfireframework.baseutil;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.jfireframework.baseutil.uniqueid.AutumnId;
import com.jfireframework.baseutil.uniqueid.Uid;

public class IdTest
{
    @Test
    public void test()
    {
        Uid uid = AutumnId.instance();
        Set<String> set = new HashSet<String>(1000000);
        for (int i = 0; i < 1000000; i++)
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
        Uid uid = AutumnId.instance();
        for (int i = 0; i < 10; i++)
        {
            System.out.println(uid.generateDigits());
        }
    }
}
