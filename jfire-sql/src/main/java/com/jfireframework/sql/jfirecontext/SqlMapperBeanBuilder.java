package com.jfireframework.sql.jfirecontext;

import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.BeanBuilder;
import com.jfireframework.context.bean.BeanConfig;

public class SqlMapperBeanBuilder implements BeanBuilder
{
    
    @Override
    public <T> Bean parse(Class<T> ckass)
    {
        String beanName = ckass.getName() + "$Mapper";
        Bean bean = new Bean(beanName, false, SqlMapperHolder.class, ckass);
        BeanConfig beanConfig = new BeanConfig(beanName);
        beanConfig.putParam("ckass", ckass.getName());
        bean.setBeanConfig(beanConfig);
        return bean;
    }
    
}
