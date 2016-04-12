package com.jfireframework.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.core.ViewAndModel;
import com.jfireframework.mvc.util.BeetlRender;
import com.jfireframework.mvc.util.JfireMvcResponseWrapper;

public class BeetlView implements View
{
    private BeetlRender beetlRender;
    
    public BeetlView(BeetlRender beetlRender)
    {
        this.beetlRender = beetlRender;
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        ViewAndModel viewAndModel = (ViewAndModel) result;
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
                    beetlRender.render(viewAndModel, request, wrapper);
                    wrapper.getOutputStream().flush();
                    viewAndModel.setDirectBytes(viewAndModel.getCache().toArray());
                }
            }
        }
        else
        {
            beetlRender.render(viewAndModel, request, response);
        }
    }
}
