package com.jfireframework.litl.template;

import java.util.Map;
import com.jfireframework.litl.TplCenter;

public interface Template
{
    public LineInfo[] getContent();
    
    public boolean isModified();
    
    public String render(Map<String, Object> data);
    
    public TplCenter geTplCenter();
    
    public String getName();
}
