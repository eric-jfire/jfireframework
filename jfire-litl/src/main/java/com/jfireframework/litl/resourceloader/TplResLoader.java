package com.jfireframework.litl.resourceloader;

import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.template.Template;

public interface TplResLoader
{
    /**
     * 使用名称定位一个模板文件
     * 
     * @param path
     * @return
     */
    public Template loadTemplate(String path, TplCenter tplCenter);
}
