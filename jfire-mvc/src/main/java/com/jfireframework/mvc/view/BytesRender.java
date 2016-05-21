package com.jfireframework.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BytesRender implements ViewRender
{
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        response.getOutputStream().write((byte[]) result);
    }
    
}
