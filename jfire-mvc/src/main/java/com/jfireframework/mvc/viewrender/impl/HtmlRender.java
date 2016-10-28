package com.jfireframework.mvc.viewrender.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.mvc.core.ModelAndView;
import com.jfireframework.mvc.util.JfireMvcResponseWrapper;
import com.jfireframework.mvc.viewrender.ViewRender;

public class HtmlRender implements ViewRender
{
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        if (result instanceof ModelAndView)
        {
            ModelAndView viewAndModel = (ModelAndView) result;
            response.setContentType("text/html");
            if (viewAndModel.cached())
            {
                response.getOutputStream().write(viewAndModel.getDirectBytes());
            }
            else if (viewAndModel.isDirect())
            {
                synchronized (viewAndModel)
                {
                    
                    if (viewAndModel.cached())
                    {
                        response.getOutputStream().write(viewAndModel.getDirectBytes());
                    }
                    else
                    {
                        JfireMvcResponseWrapper wrapper = new JfireMvcResponseWrapper(response, viewAndModel);
                        request.getRequestDispatcher(viewAndModel.getModelName()).forward(request, wrapper);
                        wrapper.getOutputStream().flush();
                        viewAndModel.setDirectBytes(viewAndModel.getCache().toArray());
                    }
                }
            }
            else
            {
                request.getRequestDispatcher(viewAndModel.getModelName()).forward(request, response);
            }
        }
        else if (result instanceof String)
        {
            request.getRequestDispatcher((String) result).forward(request, response);
        }
        else
        {
            throw new UnSupportException("不支持的返回类型");
        }
    }
}
