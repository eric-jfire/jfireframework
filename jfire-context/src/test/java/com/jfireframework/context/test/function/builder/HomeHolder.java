package com.jfireframework.context.test.function.builder;

import com.jfireframework.context.bean.BeanInstanceHolder;

public class HomeHolder implements BeanInstanceHolder
{
    private Home home = new Home() {
        
        @Override
        public int getLength()
        {
            return 100;
        }
    };
    
    @Override
    public Object getObject()
    {
        return home;
    }
    
}
