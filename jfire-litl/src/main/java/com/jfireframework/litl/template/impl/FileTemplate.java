package com.jfireframework.litl.template.impl;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
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
    private final File      file;
    private Queue<LineInfo> content;
    private volatile long   lastModifyTime;
    private final TplCenter tplCenter;
    private final boolean   devMode;
    private final String    path;
    private volatile Output output;
    
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
        content = getContent();
        try
        {
            output = OutPutBuilder.build(new LinkedBlockingDeque<LineInfo>(content), this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JustThrowException(e);
        }
    }
    
    @Override
    public Queue<LineInfo> getContent()
    {
        if (isModified())
        {
            synchronized (file)
            {
                if (isModified())
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
                    content = lines;
                    lastModifyTime = file.lastModified();
                }
            }
        }
        return content;
    }
    
    @Override
    public boolean isModified()
    {
        return file.lastModified() != lastModifyTime;
    }
    
    @Override
    public String render(Map<String, Object> data)
    {
        if (devMode && isModified())
        {
            synchronized (file)
            {
                if (isModified())
                {
                    try
                    {
                        output = OutPutBuilder.build(new LinkedBlockingDeque<LineInfo>(getContent()), this);
                    }
                    catch (Exception e)
                    {
                        throw new UnSupportException("生成渲染类时异常", e);
                    }
                }
            }
        }
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
