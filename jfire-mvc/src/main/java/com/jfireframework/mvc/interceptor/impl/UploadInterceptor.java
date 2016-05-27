package com.jfireframework.mvc.interceptor.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.mvc.binder.UploadItem;
import com.jfireframework.mvc.core.Action;
import com.jfireframework.mvc.interceptor.ActionInterceptor;

@Resource
public class UploadInterceptor implements ActionInterceptor
{
    public static final String  uploadFileList = "UploadInterceptor_UploadItemList" + System.currentTimeMillis();
    private static final Logger logger         = ConsoleLogFactory.getLogger();
    @Resource(name = "servletContext")
    private ServletContext      servletContext;
    private float               version;
    
    @PostConstruct
    public void InitMethod()
    {
        int majorVersion = servletContext.getEffectiveMajorVersion();
        int minorVersion = servletContext.getEffectiveMinorVersion();
        version = Float.valueOf(majorVersion + "." + minorVersion);
        logger.debug("当前的Servlet容器的版本是{}", version);
    }
    
    @Override
    public int getOrder()
    {
        return 11;
    }
    
    @Override
    public boolean interceptor(HttpServletRequest request, HttpServletResponse response, Action action)
    {
        try
        {
            /**
             * 在这里必须要有这样的一句。这样可以保证如果对request进行编码设置可以生效。如果请求是multipart/form-
             * data类型， 也不会干扰对流的读取，所以是安全的。
             */
            request.getParameter("");
            String contentType = request.getContentType();
            if (StringUtil.isNotBlank(contentType) && contentType.startsWith("multipart/form-data"))
            {
                if (version == 3.0)
                {
                    List<UploadItem> list = new ArrayList<>();
                    for (Part part : request.getParts())
                    {
                        /**
                         * 该字段是上传文件的文件名，如果该字段为空，则意味着该字段是一个普通的表单字段，表单字段的信息仍然可以通过
                         * request.getParameterMap()得到. 只有在该字段是文件字段时才进行处理
                         */
                        if (part.getSize() > 0 && part.getHeader("content-disposition").indexOf("filename=") != -1)
                        {
                            list.add(buildUploadItemForServlet3(part));
                        }
                    }
                    request.setAttribute(UploadInterceptor.uploadFileList, list);
                    return true;
                }
                else if (version == 3.1)
                {
                    List<UploadItem> list = new ArrayList<>();
                    for (Part part : request.getParts())
                    {
                        /**
                         * 该字段是上传文件的文件名，如果该字段为空，则意味着该字段是一个普通的表单字段，表单字段的信息仍然可以通过
                         * request.getParameterMap()得到. 只有在该字段是文件字段时才进行处理
                         */
                        if (part.getSize() > 0 && StringUtil.isNotBlank(part.getSubmittedFileName()))
                        {
                            list.add(buildUploadItemForServlet31(part));
                        }
                    }
                    request.setAttribute(UploadInterceptor.uploadFileList, list);
                    return true;
                }
                else
                {
                    throw new RuntimeException("不支持的servlet标准:" + version + "，目前支持3.0和3.1标准");
                }
            }
            else
            {
                return true;
            }
        }
        catch (IOException | ServletException e)
        {
            logger.error("进行上传拦截出现问题", e);
            return false;
        }
    }
    
    /**
     * 通过part生成我们需要的uploadItme
     * 
     * @param part
     * @return
     */
    private UploadItem buildUploadItemForServlet31(Part part)
    {
        /**
         * 该信息是文件的原始文件名。
         * 在google和firefox浏览器下，会是直接的文件名，而在ie浏览器下，则是一个文件的文件路径，类似F:\jquery.pdf
         */
        String fileName = part.getSubmittedFileName();
        fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        String fieldName = part.getName();
        UploadItem item = new UploadItem(part, fileName, fieldName);
        return item;
    }
    
    private UploadItem buildUploadItemForServlet3(Part part)
    {
        String header = part.getHeader("content-disposition");
        /*
         * String[] tempArr1 =
         * header.split(";");代码执行完之后，在不同的浏览器下，tempArr1数组里面的内容稍有区别
         * 火狐或者google浏览器下：tempArr1={form-data,name="file",filename=
         * "snmp4j--api.zip"}
         * IE浏览器下：tempArr1={form-data,name="file",filename="E:\snmp4j--api.zip"}
         */
        String[] tempArr1 = header.split(";");
        /**
         * 火狐或者google浏览器下：tempArr2={filename,"snmp4j--api.zip"}
         * IE浏览器下：tempArr2={filename,"E:\snmp4j--api.zip"}
         */
        String[] tempArr2 = tempArr1[2].split("=");
        // 获取文件名，兼容各种浏览器的写法
        String fileName = tempArr2[1].substring(tempArr2[1].lastIndexOf("\\") + 1).replaceAll("\"", "");
        String fieldName = part.getName();
        UploadItem item = new UploadItem(part, fileName, fieldName);
        return item;
    }
    
    @Override
    public String pathRule()
    {
        return "*";
    }
    
    @Override
    public String tokenRule()
    {
        return null;
    }
}
