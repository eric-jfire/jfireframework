package com.jfireframework.mvc.view;

import java.io.OutputStream;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.codejson.JsonTool;

public class JsonRender implements ViewRender
{
    private final Charset charset;
    
    public JsonRender(Charset charset)
    {
        this.charset = charset;
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        response.setContentType("application/json");
        OutputStream out = response.getOutputStream();
        out.write(JsonTool.write(result).getBytes(charset));
    }
}
