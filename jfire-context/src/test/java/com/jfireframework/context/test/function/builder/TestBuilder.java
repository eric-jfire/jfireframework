package com.jfireframework.context.test.function.builder;

import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.BeanBuilder;

public class TestBuilder implements BeanBuilder
{
    
    @Override
    public <T> Bean parse(Class<T> ckass)
    {
        if (ckass == Person.class)
        {
            return new Bean("person", false, PersonHolder.class, Person.class);
        }
        else
        {
            return new Bean("home", false, HomeHolder.class, Home.class);
        }
    }
    
}
