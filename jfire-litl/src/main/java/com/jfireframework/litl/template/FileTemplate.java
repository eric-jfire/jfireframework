package com.jfireframework.litl.template;

import java.io.File;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.LineReader;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.tplrender.RenderBuilder;
import com.jfireframework.litl.tplrender.TplRender;

public class FileTemplate implements Template
{
    private final File         file;
    private LineInfo[]         content;
    private volatile long      lastModifyTime;
    private final TplCenter    tplCenter;
    private volatile TplRender render;
    private final boolean      devMode;
    
    public FileTemplate(File file, TplCenter tplCenter)
    {
        this.file = file;
        this.tplCenter = tplCenter;
        devMode = tplCenter.isDevMode();
        content = buildLineInfos();
    }
    
    @Override
    public LineInfo[] getContent()
    {
        if (isModified())
        {
            synchronized (file)
            {
                if (isModified())
                {
                    content = buildLineInfos();
                    lastModifyTime = file.lastModified();
                }
            }
        }
        return content;
    }
    
    public LineInfo[] buildLineInfos()
    {
        LineReader reader = new LineReader(file, Charset.forName("utf8"));
        String value = null;
        int line = 1;
        List<LineInfo> lines = new LinkedList<LineInfo>();
        while ((value = reader.readLine()) != null)
        {
            LineInfo lineContext = new LineInfo(line, value);
            lines.add(lineContext);
            line += 1;
        }
        reader.close();
        return lines.toArray(new LineInfo[lines.size()]);
    }
    
    @Override
    public boolean isModified()
    {
        return file.lastModified() != lastModifyTime;
    }
    
    @Override
    public String render(Map<String, Object> data)
    {
        if (render == null)
        {
            synchronized (tplCenter)
            {
                if (render == null)
                {
                    try
                    {
                        render = RenderBuilder.build(data, this);
                    }
                    catch (Exception e)
                    {
                        throw new UnSupportException("生成渲染类时异常", e);
                    }
                }
            }
        }
        if (devMode && isModified())
        {
            synchronized (tplCenter)
            {
                if (isModified())
                {
                    try
                    {
                        render = RenderBuilder.build(data, this);
                    }
                    catch (Exception e)
                    {
                        throw new UnSupportException("生成渲染类时异常", e);
                    }
                }
            }
        }
        return render.render(data);
    }
    
    @Override
    public TplCenter geTplCenter()
    {
        return tplCenter;
    }
    
    @Override
    public String getName()
    {
        return file.getName();
    }
    
}
