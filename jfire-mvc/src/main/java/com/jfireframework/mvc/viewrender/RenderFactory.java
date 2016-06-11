package com.jfireframework.mvc.viewrender;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.mvc.config.ResultType;
import com.jfireframework.mvc.core.ModelAndView;
import com.jfireframework.mvc.util.BeetlRender;
import com.jfireframework.mvc.util.JfireMvcResponseWrapper;

public class RenderFactory
{
    
    private final BeetlRender    beetlRender;
    private final BytesRender    bytesRender;
    private final JsonRender     JsonRender;
    private final StringRender   stringRender;
    private final HtmlRender     htmlRender;
    private final JspRender      jspRender;
    private final RedirectRender redirectRender;
    private final NoneRender     noneRender;
    
    public RenderFactory(Charset charset, BeetlRender beetlRender)
    {
        this.beetlRender = beetlRender;
        bytesRender = new BytesRender();
        htmlRender = new HtmlRender();
        JsonRender = new JsonRender(charset);
        jspRender = new JspRender();
        noneRender = new NoneRender();
        redirectRender = new RedirectRender();
        stringRender = new StringRender(charset);
    }
    
    public ViewRender getViewRender(ResultType resultType)
    {
        switch (resultType)
        {
            case Beetl:
                return beetlRender;
            case Bytes:
                return bytesRender;
            case Html:
                return htmlRender;
            case Json:
                return JsonRender;
            case Jsp:
                return jspRender;
            case None:
                return noneRender;
            case Redirect:
                return redirectRender;
            case String:
                return stringRender;
            case FreeMakrer:
                throw new UnSupportException("不支持FreeMarker，建议使用Beetl");
            default:
                throw new UnSupportException("不应该走到这个分支");
        }
    }
    
    class BytesRender implements ViewRender
    {
        
        @Override
        public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
        {
            response.getOutputStream().write((byte[]) result);
        }
        
    }
    
    class HtmlRender implements ViewRender
    {
        
        @Override
        public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
        {
            if (result instanceof ModelAndView)
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
                            request.getRequestDispatcher(viewAndModel.getModelName()).forward(request, wrapper);
                            wrapper.getOutputStream().flush();
                            viewAndModel.setDirectBytes(viewAndModel.getCache().toArray());
                        }
                    }
                }
                else
                {
                    request.getRequestDispatcher(viewAndModel.getModelName()).forward(request, response);
                }
            }
            else if (result instanceof String)
            {
                request.getRequestDispatcher((String) result).forward(request, response);
            }
            else
            {
                throw new UnSupportException("不支持的返回类型");
            }
        }
    }
    
    class JsonRender implements ViewRender
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
    
    class JspRender implements ViewRender
    {
        
        @Override
        public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
        {
            ModelAndView viewAndModel = (ModelAndView) result;
            for (Entry<String, Object> entry : viewAndModel.getData().entrySet())
            {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
            request.getRequestDispatcher(viewAndModel.getModelName()).forward(request, response);
        }
        
    }
    
    class NoneRender implements ViewRender
    {
        
        @Override
        public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
        {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    class RedirectRender implements ViewRender
    {
        
        @Override
        public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
        {
            response.sendRedirect((String) result);
        }
        
    }
    
    class StringRender implements ViewRender
    {
        private final Charset charset;
        
        public StringRender(Charset charset)
        {
            this.charset = charset;
        }
        
        @Override
        public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
        {
            response.getOutputStream().write(((String) result).getBytes(charset));
            
        }
    }
    
}
