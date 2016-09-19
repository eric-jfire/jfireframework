package com.jfireframework.baseutil;

import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;

public class Logtest
{
    private static final Logger logger = ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
    
    public void demo()
    {
        logger.debug("sdas");
    }
    
    public static void main(String[] args)
    {
        new Logtest().demo();
    }
}
