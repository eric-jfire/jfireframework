package com.jfireframework.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.jfireframework.mvc.config.ResultType;
import com.jfireframework.mvc.util.RequestMethod;

/**
 * 表示该方法是一个action方法
 * 
 * @author 林斌（windfire@zailanghua.com）
 * 
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping
{
    /**
     * 请求路径，默认不填写的话为方法名称
     * 
     * @return
     */
    public String value() default "";
    
    /**
     * 视图类型
     * 
     * @return
     * @author windfire(windfire@zailanghua.com)
     */
    public ResultType resultType() default ResultType.AUTO;
    
    /**
     * 该方法直接从request中读取流，不进行数据组装处理
     * 
     * @return
     */
    public boolean readStream() default false;
    
    /**
     * 是否是rest风格的url
     * 
     * @return
     */
    public boolean rest() default false;
    
    public RequestMethod method() default RequestMethod.GET;
    
    /**
     * 默认为返回类型为text/html
     * 
     * @return
     */
    public String contentType() default "";
    
    /**
     * 该方法映射的别名。特定的场合下，可以用来做唯一识别
     * 
     * @return
     */
    public String token() default "";
    
}
