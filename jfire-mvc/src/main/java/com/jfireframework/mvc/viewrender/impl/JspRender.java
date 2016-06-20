package com.jfireframework.mvc.viewrender.impl;

import java.nio.charset.Charset;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.core.ModelAndView;
import com.jfireframework.mvc.viewrender.ViewRender;

public class JspRender implements ViewRender
{
    public JspRender(Charset charset, ClassLoader classLoader)
    {
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        ModelAndView viewAndModel = (ModelAndView) result;
        for (Entry<String, Object> entry : viewAndModel.getData().entrySet())
        {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        request.getRequestDispatcher(viewAndModel.getModelName()).forward(request, response);
    }
    
}
