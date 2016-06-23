package com.jfireframework.mvc.viewrender.impl;

import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.resourceloader.TplResLoader;
import com.jfireframework.litl.resourceloader.WebAppResLoader;
import com.jfireframework.mvc.core.ModelAndView;
import com.jfireframework.mvc.viewrender.ViewRender;

public class LitlRender implements ViewRender
{
    private final TplCenter tplCenter;
    private final Charset   charset;
    
    public LitlRender(Charset charset, ClassLoader classLoader)
    {
        TplResLoader loader = new WebAppResLoader();
        tplCenter = new TplCenter(loader, classLoader);
        this.charset = charset;
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        ModelAndView vm = (ModelAndView) result;
        vm.addObject("ctx", request.getContextPath());
        vm.addObject("ctxPath", request.getContextPath());
        String value = tplCenter.load(vm.getModelName()).render(vm.getData());
        response.getOutputStream().write(value.getBytes(charset));
        response.getOutputStream().flush();
    }
    
}
