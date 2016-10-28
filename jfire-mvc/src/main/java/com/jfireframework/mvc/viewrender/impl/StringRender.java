package com.jfireframework.mvc.viewrender.impl;

import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.viewrender.ViewRender;

public class StringRender implements ViewRender
{
    private final Charset charset;
    
    public StringRender(Charset charset)
    {
        this.charset = charset;
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        response.getOutputStream().write(((String) result).getBytes(charset));
        
    }
}
