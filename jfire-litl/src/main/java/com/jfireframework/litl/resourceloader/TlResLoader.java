package com.jfireframework.litl.resourceloader;

import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.template.Template;

public interface TlResLoader
{
    /**
     * 使用名称定位一个模板文件
     * 
     * @param name
     * @return
     */
    public Template loadTemplate(String name, TplCenter tplCenter);
}
