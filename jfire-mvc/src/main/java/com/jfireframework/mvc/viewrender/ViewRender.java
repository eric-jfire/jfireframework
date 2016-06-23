package com.jfireframework.mvc.viewrender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ViewRender
{
    /**
     * 对视图进行渲染
     */
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable;
}
