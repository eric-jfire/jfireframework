package com.jfireframework.context.test.function.beanfactory;

import javax.annotation.Resource;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.context.aliasanno.AnnotationUtil;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.BeanFactory;

public class TestFactory implements BeanFactory
{
    
    @Override
    public Bean parse(Class<?> ckass)
    {
        if (ckass == Person.class)
        {
            Resource resource = AnnotationUtil.getAnnotation(Resource.class, ckass);
            return new Bean(resource.name(), new personimple());
        }
        else
        {
            throw new UnSupportException("");
        }
    }
    
    class personimple implements Person
    {
        
        @Override
        public String getName()
        {
            return "aaaa";
        }
        
    }
}
