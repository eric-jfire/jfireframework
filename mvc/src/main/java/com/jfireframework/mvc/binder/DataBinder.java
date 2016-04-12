package com.jfireframework.mvc.binder;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 数据绑定类，用来对request中的参数进行解析并且生成绑定参数后的对象
 * 
 * @author 林斌（windfire@zailanghua.com）
 * 
 */
public interface DataBinder
{
    /**
     * 从request中获取信息并且完成参数的绑定，将绑定后的参数返回
     * 
     * @param map TODO
     * @param response TODO
     * 
     * @return
     */
    public Object binder(HttpServletRequest request, Map<String, String> map, HttpServletResponse response);
    
    public String getParamName();
}
