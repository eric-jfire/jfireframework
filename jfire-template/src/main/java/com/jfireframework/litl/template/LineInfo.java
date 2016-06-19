package com.jfireframework.litl.template;

public class LineInfo
{
    private final int    line;
    private final String content;
    
    public LineInfo(int line, String content)
    {
        this.line = line;
        this.content = content;
    }
    
    public int getLine()
    {
        return line;
    }
    
    public String getContent()
    {
        return content;
    }
    
}
