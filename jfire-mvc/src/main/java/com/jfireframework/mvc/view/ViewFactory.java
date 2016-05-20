package com.jfireframework.mvc.view;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.mvc.config.ResultType;
import com.jfireframework.mvc.core.ViewAndModel;
import com.jfireframework.mvc.util.BeetlRender;
import com.jfireframework.mvc.util.JfireMvcResponseWrapper;

public class ViewFactory
{
    
    private final BeetlView    beetlView;
    private final BytesView    bytesView;
    private final JsonView     JsonView;
    private final StringView   stringView;
    private final HtmlView     htmlView;
    private final JspView      jspView;
    private final RedirectView redirectView;
    private final NoneView     noneView;
    
    public ViewFactory(Charset charset, BeetlRender beetlRender)
    {
        beetlView = new BeetlView(beetlRender);
        bytesView = new BytesView();
        htmlView = new HtmlView();
        JsonView = new JsonView(charset);
        jspView = new JspView();
        noneView = new NoneView();
        redirectView = new RedirectView();
        stringView = new StringView(charset);
    }
    
    public View getViewRender(ResultType resultType)
    {
        switch (resultType)
        {
            case Beetl:
                return beetlView;
            case Bytes:
                return bytesView;
            case Html:
                return htmlView;
            case Json:
                return JsonView;
            case Jsp:
                return jspView;
            case None:
                return noneView;
            case Redirect:
                return redirectView;
            case String:
                return stringView;
            case FreeMakrer:
                throw new UnSupportException("不支持FreeMarker，建议使用Beetl");
            default:
                throw new UnSupportException("不应该走到这个分支");
        }
    }
    
    class StringView implements View
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
    
    class RedirectView implements View
    {
        
        @Override
        public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
        {
            response.sendRedirect((String) result);
        }
        
    }
    
    class NoneView implements View
    {
        
        @Override
        public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
        {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    class JspView implements View
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
    
    class JsonView implements View
    {
        private final Charset charset;
        
        public JsonView(Charset charset)
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
    
    class HtmlView implements View
    {
        
        @Override
        public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
        {
            ViewAndModel viewAndModel = (ViewAndModel) result;
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
    }
    
    class BytesView implements View
    {
        
        @Override
        public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
        {
            response.getOutputStream().write((byte[]) result);
        }
        
    }
    
    class BeetlView implements View
    {
        private BeetlRender beetlRender;
        
        public BeetlView(BeetlRender beetlRender)
        {
            this.beetlRender = beetlRender;
        }
        
        @Override
        public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
        {
            ViewAndModel viewAndModel = (ViewAndModel) result;
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
                        beetlRender.render(viewAndModel, request, wrapper);
                        wrapper.getOutputStream().flush();
                        viewAndModel.setDirectBytes(viewAndModel.getCache().toArray());
                    }
                }
            }
            else
            {
                beetlRender.render(viewAndModel, request, response);
            }
        }
    }
    
}
