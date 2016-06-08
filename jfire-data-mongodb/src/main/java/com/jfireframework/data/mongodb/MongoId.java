package com.jfireframework.data.mongodb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.FIELD)
public @interface MongoId
{
    /**
     * mongodb中的id名称,默认为_id
     * 
     * @return
     */
    public String value() default "_id";
}
