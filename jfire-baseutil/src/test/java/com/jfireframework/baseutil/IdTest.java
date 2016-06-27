package com.jfireframework.baseutil;

import com.jfireframework.baseutil.uniqueid.SummerId;
import com.jfireframework.baseutil.uniqueid.Uid;
import sun.applet.Main;

public class IdTest
{
    public static void main(String[] args)
    {
        Uid uid = new SummerId(1);
        System.out.println(uid.generateDigits());
        System.out.println(uid.generateDigits());
        System.out.println(uid.generateDigits());
        System.out.println(uid.generateDigits());
        System.out.println(uid.generateDigits());
    }
}
