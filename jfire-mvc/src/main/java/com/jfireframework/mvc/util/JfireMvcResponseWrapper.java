package com.jfireframework.mvc.util;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.core.ModelAndView;

public class JfireMvcResponseWrapper extends javax.servlet.http.HttpServletResponseWrapper
{
    private ModelAndView vm;
    
    public JfireMvcResponseWrapper(HttpServletResponse response, ModelAndView vm)
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
    private ModelAndView        vm;
    
    public ServletOutputStreamWrapper(ServletOutputStream os, ModelAndView vm)
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
