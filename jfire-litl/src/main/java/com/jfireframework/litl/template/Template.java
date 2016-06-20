package com.jfireframework.litl.template;

import java.util.Map;
import com.jfireframework.litl.TplCenter;

public interface Template
{
    public LineInfo[] getContent();
    
    public Template load(String name);
    
    /**
     * 返回模板文件所在文件夹的路径
     * 
     * @return
     */
    
    public boolean isModified();
    
    public String render(Map<String, Object> data);
    
    public TplCenter getTplCenter();
    
    /**
     * 模板的查询路径。也就是模板对应的key。这是不包含查询根目录的
     * 
     * @return
     */
    public String getPath();
}
