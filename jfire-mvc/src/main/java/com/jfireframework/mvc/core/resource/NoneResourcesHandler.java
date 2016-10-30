package com.jfireframework.mvc.core.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoneResourcesHandler  implements ResourcesHandler
{

    @Override
    public boolean handle(HttpServletRequest request, HttpServletResponse response)
    {
        return false;
    }
    
}
