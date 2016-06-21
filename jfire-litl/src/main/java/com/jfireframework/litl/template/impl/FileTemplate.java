package com.jfireframework.litl.template.impl;

import java.io.File;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import com.jfireframework.baseutil.LineReader;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.output.OutPutBuilder;
import com.jfireframework.litl.output.Output;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class FileTemplate implements Template
{
    private final File         file;
    private LineInfo[]         content;
    private volatile long      lastModifyTime;
    private final TplCenter    tplCenter;
    private final boolean      devMode;
    private final String       path;
    private final Output       output;
    
    public FileTemplate(File file, String path, TplCenter tplCenter)
    {
        this.file = file;
        if (this.file.exists() == false)
        {
            throw new UnSupportException(StringUtil.format("找不到模板文件:{},模板查找根目录是:{}", path, tplCenter.getRootPath()));
        }
        this.path = path;
        this.tplCenter = tplCenter;
        devMode = tplCenter.isDevMode();
        content = buildLineInfos();
        try
        {
            // render = tplCenter.getBuilder().build(null, this);
            output = new OutPutBuilder().build(buildLineInfos0(), this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JustThrowException(e);
        }
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
    
    public Queue<LineInfo> buildLineInfos0()
    {
        LineReader reader = new LineReader(file, Charset.forName("utf8"));
        String value = null;
        int line = 1;
        Queue<LineInfo> lines = new LinkedBlockingQueue<LineInfo>();
        while ((value = reader.readLine()) != null)
        {
            LineInfo lineContext = new LineInfo(line, value);
            lines.add(lineContext);
            line += 1;
        }
        reader.close();
        return lines;
    }
    
    @Override
    public boolean isModified()
    {
        return file.lastModified() != lastModifyTime;
    }
    
    @Override
    public String render(Map<String, Object> data)
    {
        // if (render == null)
        // {
        // synchronized (tplCenter)
        // {
        // if (render == null)
        // {
        // try
        // {
        // render = tplCenter.getBuilder().build(data, this);
        // }
        // catch (Exception e)
        // {
        // throw new UnSupportException("生成渲染类时异常", e);
        // }
        // }
        // }
        // }
        // if (devMode && isModified())
        // {
        // synchronized (tplCenter)
        // {
        // if (isModified())
        // {
        // try
        // {
        // render = tplCenter.getBuilder().build(data, this);
        // }
        // catch (Exception e)
        // {
        // throw new UnSupportException("生成渲染类时异常", e);
        // }
        // }
        // }
        // }
        // return render.render(data);
        StringCache cache = new StringCache();
        output.output(cache, data);
        return cache.toString();
    }
    
    @Override
    public TplCenter getTplCenter()
    {
        return tplCenter;
    }
    
    @Override
    public Template load(String name)
    {
        if (name.charAt(0) == '/')
        {
            return tplCenter.load(name);
        }
        else
        {
            String rootPath = tplCenter.getRootPath();
            String filePath = file.getParentFile().getAbsolutePath() + File.separatorChar + name;
            String keyPath = filePath.substring(rootPath.length());
            
            return tplCenter.load(keyPath);
        }
    }
    
    @Override
    public String getPath()
    {
        return path;
    }
    
}
