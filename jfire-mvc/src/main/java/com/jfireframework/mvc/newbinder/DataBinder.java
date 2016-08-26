package com.jfireframework.mvc.newbinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    public Object binder(HttpServletRequest request, TreeValueNode treeValueNode, HttpServletResponse response);
    
    public String getParamName();
}
