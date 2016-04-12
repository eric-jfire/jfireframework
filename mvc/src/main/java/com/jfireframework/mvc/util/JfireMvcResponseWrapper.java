package com.jfireframework.mvc.util;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.core.ViewAndModel;

public class JfireMvcResponseWrapper extends javax.servlet.http.HttpServletResponseWrapper
{
    private ViewAndModel vm;
    
    public JfireMvcResponseWrapper(HttpServletResponse response, ViewAndModel vm)
    {
        super(response);
        this.vm = vm;
    }
    
    public ServletOutputStream getOutputStream() throws IOException
    {
        return new ServletOutputStreamWrapper(super.getOutputStream(), vm);
    }
}

class ServletOutputStreamWrapper extends ServletOutputStream
{
    
    private ServletOutputStream os;
    private ViewAndModel        vm;
    
    public ServletOutputStreamWrapper(ServletOutputStream os, ViewAndModel vm)
    {
        this.os = os;
        this.vm = vm;
    }
    
    @Override
    public boolean isReady()
    {
        return os.isReady();
    }
    
    @Override
    public void setWriteListener(WriteListener writeListener)
    {
        os.setWriteListener(writeListener);
    }
    
    @Override
    public void write(int b) throws IOException
    {
        os.write(b);
        vm.getCache().put((byte) b);
    }
    
}
