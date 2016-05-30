package com.jfireframework.context.test.function;

import javax.annotation.Resource;
import com.jfireframework.context.util.AliasFor;

@TestAlias
public @interface Testalis3
{
    @AliasFor(annotation = TestAlias.class, value = "test")
    public String value();
    
    @AliasFor(annotation = Resource.class, value = "sharable")
    public boolean s() default false;
}
