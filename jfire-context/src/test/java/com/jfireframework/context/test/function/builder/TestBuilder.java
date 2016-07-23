package com.jfireframework.context.test.function.builder;

import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.BeanBuilder;
import com.jfireframework.context.bean.BeanConfig;

public class TestBuilder implements BeanBuilder
{
    
    @Override
    public <T> Bean parse(Class<T> ckass)
    {
        if (ckass == Person.class)
        {
            Bean bean = new Bean("person", false, AllHolder.class, Person.class);
            BeanConfig beanConfig = new BeanConfig("person");
            beanConfig.putParam("ckass", Person.class.getCanonicalName());
            bean.setBeanConfig(beanConfig);
            return bean;
        }
        else
        {
            Bean bean = new Bean("home", false, AllHolder.class, Home.class);
            BeanConfig beanConfig = new BeanConfig("home");
            beanConfig.putParam("ckass", Home.class.getCanonicalName());
            bean.setBeanConfig(beanConfig);
            return bean;
        }
    }
    
}
