package com.jfireframework.mvc.view;

import java.io.IOException;
import java.nio.charset.Charset;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.WebAppResourceLoader;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.mvc.config.ResultType;

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
    
    public RenderFactory(Charset charset)
    {
        WebAppResourceLoader loader = new WebAppResourceLoader();
        Configuration configuration = null;
        try
        {
            configuration = Configuration.defaultConfiguration();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        beetlRender = new BeetlRender(new GroupTemplate(loader, configuration));
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
    
}
