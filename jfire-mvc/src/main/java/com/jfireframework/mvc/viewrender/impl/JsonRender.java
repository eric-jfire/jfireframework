package com.jfireframework.mvc.viewrender.impl;

import java.io.OutputStream;
import java.nio.charset.Charset;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.mvc.viewrender.ViewRender;

@Resource
public class JsonRender implements ViewRender
{
    @Resource
    private Charset charset;
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        response.setContentType("application/json");
        OutputStream out = response.getOutputStream();
        out.write(JsonTool.write(result).getBytes(charset));
    }
}
