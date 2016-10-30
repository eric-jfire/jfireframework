package com.jfireframework.mvc.core.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ResourcesHandler
{
    /**
     * 进行静态资源的处理，如果没有的话，返回false
     * 
     * @param request
     * @param response
     * @return
     */
    public boolean handle(HttpServletRequest request, HttpServletResponse response);
}
