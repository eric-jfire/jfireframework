package com.jfireframework.context.test.function.aliastest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Resource;
import com.jfireframework.context.aliasanno.AliasFor;

@Resource
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired
{
    @AliasFor(annotation = Resource.class, value = "name")
    public String wiredName();
}
