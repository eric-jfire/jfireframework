package com.jfireframework.sql.jfirecontext;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Resource;
import com.jfireframework.context.bean.load.LoadBy;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
@Documented
@Inherited
@Resource
@LoadBy(factoryBeanName = "sessionFactory")
public @interface MapperOp
{
    
}
