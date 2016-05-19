package com.jfireframework.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.order.Order;
import com.jfireframework.mvc.core.Action;

public interface ActionInterceptor extends Order
{
    
    /**
     * 对请求的action进行拦截 如果返回为false，请求无法通过。不予处理
     * 
     * @param request
     * @param response
     */
    public boolean interceptor(HttpServletRequest request, HttpServletResponse response, Action action);
    
    /**
     * 返回需要进行前置拦截的路径，*代表拦截所有。匹配的时候是从前到后的匹配方式。多个规则之间可以使用;进行间隔
     * 
     * @return
     */
    public String rule();
}
