package com.jfireframework.mvc.view;

import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.core.ViewAndModel;

public class JspView implements View
{
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        ViewAndModel viewAndModel = (ViewAndModel) result;
        for (Entry<String, Object> entry : viewAndModel.getData().entrySet())
        {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        request.getRequestDispatcher(viewAndModel.getModelName()).forward(request, response);
    }
    
}
