package com.jfireframework.mvc.viewrender.impl;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.resourceloader.TplResLoader;
import com.jfireframework.litl.resourceloader.WebAppResLoader;
import com.jfireframework.mvc.core.ModelAndView;
import com.jfireframework.mvc.util.JfireMvcResponseWrapper;
import com.jfireframework.mvc.viewrender.ViewRender;

public class LitlRender implements ViewRender
{
    private final TplCenter tplCenter;
    private final Charset   charset;
    
    public LitlRender(Charset charset, ClassLoader classLoader)
    {
        this.charset = charset;
        TplResLoader resLoader = new WebAppResLoader();
        tplCenter = new TplCenter(resLoader, classLoader);
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
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
                    render(viewAndModel, request, wrapper);
                    wrapper.getOutputStream().flush();
                    viewAndModel.setDirectBytes(viewAndModel.getCache().toArray());
                }
            }
        }
        else
        {
            render(viewAndModel, request, response);
        }
    }
    
    /**
     * @param key 模板资源id
     * @param request
     * @param response
     * @param args 其他参数，将会传给modifyTemplate方法
     */
    public void render(ModelAndView vm, HttpServletRequest request, HttpServletResponse response)
    {
        String key = vm.getModelName();
        Map<String, Object> data = vm.getData();
        data.put("ctxPath", request.getContextPath());
        data.put("ctx", request.getContextPath());
        
        try (OutputStream os = response.getOutputStream())
        {
            String value = tplCenter.load(key).render(data);
            os.write(value.getBytes(charset));
            os.flush();
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
}
