package com.jfireframework.mvc.viewrender;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.mvc.config.ResultType;

public class RenderFactory
{
    private static Constructor<?>              beetl;
    private static Constructor<?>              bytes;
    private static Constructor<?>              html;
    private static Constructor<?>              json;
    private static Constructor<?>              jsp;
    private static Constructor<?>              none;
    private static Constructor<?>              redirect;
    private static Constructor<?>              string;
    private static Constructor<?>              litl;
    private static Map<ResultType, ViewRender> map = new HashMap<>();
    
    public static void clear()
    {
        map.clear();
    }
    
    static
    {
        try
        {
            beetl = Class.forName("com.jfireframework.mvc.viewrender.impl.BeetlRender").getConstructor(Charset.class, ClassLoader.class);
            bytes = Class.forName("com.jfireframework.mvc.viewrender.impl.BytesRender").getConstructor(Charset.class, ClassLoader.class);
            html = Class.forName("com.jfireframework.mvc.viewrender.impl.HtmlRender").getConstructor(Charset.class, ClassLoader.class);
            json = Class.forName("com.jfireframework.mvc.viewrender.impl.JsonRender").getConstructor(Charset.class, ClassLoader.class);
            jsp = Class.forName("com.jfireframework.mvc.viewrender.impl.JspRender").getConstructor(Charset.class, ClassLoader.class);
            none = Class.forName("com.jfireframework.mvc.viewrender.impl.NoneRender").getConstructor(Charset.class, ClassLoader.class);
            redirect = Class.forName("com.jfireframework.mvc.viewrender.impl.RedirectRender").getConstructor(Charset.class, ClassLoader.class);
            string = Class.forName("com.jfireframework.mvc.viewrender.impl.StringRender").getConstructor(Charset.class, ClassLoader.class);
            litl = Class.forName("com.jfireframework.mvc.viewrender.impl.LitlRender").getConstructor(Charset.class, ClassLoader.class);
        }
        catch (NoSuchMethodException | SecurityException | ClassNotFoundException e)
        {
            throw new UnSupportException("", e);
        }
    }
    
    public static ViewRender getViewRender(ResultType resultType, Charset charset, ClassLoader classLoader)
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
                case LITL:
                    viewRender = (ViewRender) litl.newInstance(charset, classLoader);
                    break;
                case Beetl:
                    viewRender = (ViewRender) beetl.newInstance(charset, classLoader);
                    break;
                case Bytes:
                    viewRender = (ViewRender) bytes.newInstance(charset, classLoader);
                    break;
                case Html:
                    viewRender = (ViewRender) html.newInstance(charset, classLoader);
                    break;
                case Json:
                    viewRender = (ViewRender) json.newInstance(charset, classLoader);
                    break;
                case Jsp:
                    viewRender = (ViewRender) jsp.newInstance(charset, classLoader);
                    break;
                case None:
                    viewRender = (ViewRender) none.newInstance(charset, classLoader);
                    break;
                case Redirect:
                    viewRender = (ViewRender) redirect.newInstance(charset, classLoader);
                    break;
                case String:
                    viewRender = (ViewRender) string.newInstance(charset, classLoader);
                    break;
                case FreeMakrer:
                    throw new UnSupportException("不支持FreeMarker，建议使用Beetl");
                default:
                    throw new UnSupportException("不应该走到这个分支");
            }
            map.put(resultType, viewRender);
            return viewRender;
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw new UnSupportException("", e);
        }
    }
    
}
