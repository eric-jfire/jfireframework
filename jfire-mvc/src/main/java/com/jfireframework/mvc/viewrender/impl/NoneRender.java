package com.jfireframework.mvc.viewrender.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.viewrender.ViewRender;

@Resource
public class NoneRender implements ViewRender
{
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        
    }
    
}
