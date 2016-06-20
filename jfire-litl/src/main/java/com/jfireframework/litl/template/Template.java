package com.jfireframework.litl.template;

import java.util.Map;
import com.jfireframework.litl.TplCenter;

public interface Template
{
    public LineInfo[] getContent();
    
    /**
     * 返回模板文件自身的文件路径
     * 
     * @return
     */
    public String getFilePath();
    
    /**
     * 返回模板文件所在文件夹的路径
     * 
     * @return
     */
    public String getDirPath();
    
    public boolean isModified();
    
    public String render(Map<String, Object> data);
    
    public TplCenter getTplCenter();
    
    public String getName();
}
