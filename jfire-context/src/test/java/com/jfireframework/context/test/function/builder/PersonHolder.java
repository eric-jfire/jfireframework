package com.jfireframework.context.test.function.builder;

import com.jfireframework.context.bean.BeanInstanceHolder;

public class PersonHolder implements BeanInstanceHolder
{
    private Person person = new Person() {
        
        @Override
        public String getName()
        {
            return "1";
        }
    };
    
    @Override
    public Object getObject()
    {
        return person;
    }
    
}
