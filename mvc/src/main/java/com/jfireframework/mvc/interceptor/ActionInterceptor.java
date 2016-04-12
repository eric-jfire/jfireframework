package com.jfireframework.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.order.Order;
import com.jfireframework.mvc.core.Action;

public interface ActionInterceptor extends Order
{
    
    /**
     * 对请求的action进行拦截
     * 如果返回为false，请求无法通过。不予处理
     * 
     * @param request
     * @param response
     */
    public boolean interceptor(HttpServletRequest request, HttpServletResponse response, Action action);
}
