package com.jfireframework.mvc.core.action;

import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.mvc.annotation.Document;

public abstract class AbstractReportMdActionListener implements ActionInitListener
{
    private static final Logger logger  = ConsoleLogFactory.getLogger();
    private String              pattarn = "\r\n"                        //
            + "|请求地址|{}|\r\n"                                           //
            + "|请求方法|{}|\r\n"                                           //
            + "|结果类型|{}|\r\n"                                           //
            + "|方法说明|{}|\r\n"                                           //
            + "|类方法签名|{}|\r\n";
    
    @Override
    public void init(Action action)
    {
        if (filter(action))
        {
            String doc;
            if (action.getMethod().isAnnotationPresent(Document.class))
            {
                doc = action.getMethod().getAnnotation(Document.class).value();
            }
            else
            {
                doc = "无";
            }
            logger.debug(pattarn, //
                    action.getRequestUrl(), //
                    action.getRequestMethod().name(), //
                    action.getResultType().name(), //
                    doc, //
                    action.getMethod().toGenericString());
        }
    }
    
    protected abstract boolean filter(Action action);
}
