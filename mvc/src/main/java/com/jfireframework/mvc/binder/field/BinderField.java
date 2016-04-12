package com.jfireframework.mvc.binder.field;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface BinderField
{
    /**
     * 将request中对应的字段的值按照正确的方式转换并且设置到entity中
     * 
     * @param request
     * @param entity
     * @param map TODO
     * @param response TODO
     * @return TODO
     * @throws InstantiationException TODO
     * @throws IllegalAccessException TODO
     */
    public Object setValue(HttpServletRequest request, Object entity, Map<String, String> map, HttpServletResponse response) throws InstantiationException, IllegalAccessException;
    
}
