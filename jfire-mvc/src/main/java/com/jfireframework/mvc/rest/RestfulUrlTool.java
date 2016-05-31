package com.jfireframework.mvc.rest;

import com.jfireframework.baseutil.collection.set.LightSet;

public class RestfulUrlTool
{
    /**
     * 根据Url创建一个RestfulRule对象
     * 
     * @param url
     * @return
     */
    public static RestfulRule build(String url)
    {
        LightSet<String> set = new LightSet<String>();
        String rule = transToRule(url, set);
        return new RestfulRule(rule, set.toArray(String.class));
    }
    
    private static String transToRule(String url, LightSet<String> set)
    {
        StringBuilder builder = new StringBuilder();
        int pre = 0, index = 0;
        do
        {
            index = url.indexOf("{", pre);
            if (index <= 0)
            {
                break;
            }
            else
            {
                builder.append(url.substring(pre, index)).append("*");
                pre = index + 1;
                index = url.indexOf("}", pre);
                if (index <= 0)
                {
                    throw new RuntimeException("");
                }
                else
                {
                    set.add(url.substring(pre, index));
                    pre = index + 1;
                }
            }
        } while (true);
        builder.append(url.substring(pre));
        return builder.toString();
    }
}
