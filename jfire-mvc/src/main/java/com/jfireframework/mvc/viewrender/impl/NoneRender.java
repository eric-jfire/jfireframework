package com.jfireframework.mvc.viewrender.impl;

import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.viewrender.ViewRender;

public class NoneRender implements ViewRender
{
    public NoneRender(Charset charset, ClassLoader classLoader)
    {
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        
    }
    
}
