package com.jfireframework.mvc.viewrender;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.mvc.config.ResultType;
import com.jfireframework.mvc.viewrender.impl.BeetlRender;
import com.jfireframework.mvc.viewrender.impl.BytesRender;
import com.jfireframework.mvc.viewrender.impl.HtmlRender;
import com.jfireframework.mvc.viewrender.impl.JsonRender;
import com.jfireframework.mvc.viewrender.impl.JspRender;
import com.jfireframework.mvc.viewrender.impl.NoneRender;
import com.jfireframework.mvc.viewrender.impl.RedirectRender;
import com.jfireframework.mvc.viewrender.impl.StringRender;

public class RenderFactory
{
    private static Map<ResultType, ViewRender> map = new HashMap<ResultType, ViewRender>();
    
    public static ViewRender getViewRender(ResultType resultType, Charset charset, ServletContext servletContext)
    {
        try
        {
            ViewRender viewRender = map.get(resultType);
            if (viewRender != null)
            {
                return viewRender;
            }
            switch (resultType)
            {
                case Beetl:
                    viewRender = new BeetlRender(servletContext);
                    break;
                case Bytes:
                    viewRender = new BytesRender();
                    break;
                case Html:
                    viewRender = new HtmlRender();
                    break;
                case Json:
                    viewRender = new JsonRender(charset);
                    break;
                case Jsp:
                    viewRender = new JspRender();
                    break;
                case None:
                    viewRender = new NoneRender();
                    break;
                case Redirect:
                    viewRender = new RedirectRender();
                    break;
                case String:
                    viewRender = new StringRender(charset);
                    break;
                case FreeMakrer:
                    throw new UnSupportException("不支持FreeMarker，建议使用Beetl");
                default:
                    throw new UnSupportException("不应该走到这个分支");
            }
            map.put(resultType, viewRender);
            return viewRender;
        }
        catch (Exception e)
        {
            throw new UnSupportException("", e);
        }
    }
    
}
