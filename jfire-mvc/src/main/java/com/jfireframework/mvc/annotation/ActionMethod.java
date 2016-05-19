package com.jfireframework.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.jfireframework.mvc.config.ResultType;

/**
 * 表示该方法是一个action方法
 * 
 * @author 林斌（windfire@zailanghua.com）
 * 
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionMethod
{
    /**
     * 请求路径，默认不填写的话为方法名称
     * 
     * @return
     */
    public String url() default "";
    
    /**
     * 视图类型
     * 
     * @return
     * @author windfire(windfire@zailanghua.com)
     */
    public ResultType resultType();
    
    /**
     * 该方法直接从request中读取流，不进行数据组装处理
     * 
     * @return
     */
    public boolean readStream() default false;
    
    public RequestMethod[] methods() default { RequestMethod.GET, RequestMethod.POST };
    
    /**
     * 默认为返回类型为text/html
     * 
     * @return
     */
    public String contentType() default "";
    
}
