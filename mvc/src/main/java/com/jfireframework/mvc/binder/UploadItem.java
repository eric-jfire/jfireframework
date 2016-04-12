package com.jfireframework.mvc.binder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.Part;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.verify.Verify;

public class UploadItem
{
    private Part   part;
    /** 页面使用的表单名称 */
    private String fieldName;
    /** 文件的真实名称 */
    private String fileName;
    private File   writedFile;
    
    public UploadItem(Part part, String fileName, String fieldName)
    {
        this.part = part;
        this.fieldName = fieldName;
        this.fileName = fileName;
    }
    
    /**
     * 获得文件的名称
     * 
     * @return
     */
    public String getFileName()
    {
        return fileName;
    }
    
    /**
     * 获取上传时表单的名称
     * 
     * @return
     */
    public String getFieldName()
    {
        return fieldName;
    }
    
    /**
     * 将文件写入指定路径，并且返回写入结果
     * 
     * @param path
     * @return
     * @throws IOException
     */
    public void write(String path)
    {
        writedFile = new File(path);
        try (FileOutputStream outputStream = new FileOutputStream(writedFile); InputStream inputStream = part.getInputStream())
        {
            byte[] data = new byte[1024 * 512];
            int length = -1;
            do
            {
                length = inputStream.read(data);
                if (length != -1)
                {
                    outputStream.write(data, 0, length);
                }
            } while (length != -1);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public String writeAndReturnMd5(String path)
    {
        writedFile = new File(path);
        try (FileOutputStream outputStream = new FileOutputStream(writedFile); InputStream inputStream = part.getInputStream())
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] data = new byte[1024 * 512];
            int length = -1;
            do
            {
                length = inputStream.read(data);
                if (length != -1)
                {
                    outputStream.write(data, 0, length);
                    md.update(data, 0, length);
                }
            } while (length != -1);
            return StringUtil.toHexString(md.digest());
        }
        catch (IOException | NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public String writeToPathUseMd5AsName(File dir)
    {
        Verify.True(dir.isDirectory(), "创建来的参数必须是一个文件路径，请检查{}", dir.getAbsoluteFile());
        byte[] src = new byte[(int) part.getSize()];
        try
        {
            part.getInputStream().read(src);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        String md5 = StringUtil.toHexString(md.digest(src));
        writedFile = new File(dir, md5);
        try (FileOutputStream outputStream = new FileOutputStream(writedFile))
        {
            outputStream.write(src);
            return md5;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        
    }
    
    public Part getPart()
    {
        return part;
    }
    
    public File getWritedFile()
    {
        return writedFile;
    }
}
