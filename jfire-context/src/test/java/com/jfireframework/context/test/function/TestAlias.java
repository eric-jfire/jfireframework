package com.jfireframework.context.test.function;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Resource;
import com.jfireframework.context.util.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Resource
public @interface TestAlias
{
    @AliasFor(annotation = Resource.class, value = "name")
    String test() default "test";
    
    @AliasFor(annotation = Resource.class, value = "sharable")
    boolean share() default false;
}
