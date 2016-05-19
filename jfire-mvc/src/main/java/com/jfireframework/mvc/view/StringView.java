package com.jfireframework.mvc.view;

import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 代表是一个直接字符串展示
 * 
 * @author 林斌{erci@jfire.cn}
 * 
 */
public class StringView implements View
{
    private final Charset charset;
    
    public StringView(Charset charset)
    {
        this.charset = charset;
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        response.getOutputStream().write(((String) result).getBytes(charset));
        
    }
}
