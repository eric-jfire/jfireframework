package com.jfireframework.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.core.ViewAndModel;

public class RedirectView implements View
{
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        response.sendRedirect(((ViewAndModel) result).getModelName());
    }
    
}
