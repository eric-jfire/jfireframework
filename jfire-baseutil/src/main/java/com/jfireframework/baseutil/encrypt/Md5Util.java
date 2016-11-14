package com.jfireframework.baseutil.encrypt;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.JustThrowException;

public class Md5Util
{
    private static Charset charset = Charset.forName("UTF-8");
    
    public static byte[] md5(byte[] array)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] result = md.digest(array);
            return result;
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    public static byte[] md5(ByteBuffer array)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(array);
            byte[] result = md.digest();
            return result;
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    public static byte[] md5(byte[] array, int off, int length)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.digest(array, off, length);
            byte[] result = md.digest();
            return result;
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new JustThrowException(e);
        }
        catch (DigestException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    public static byte[] md5(String str)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] data = str.getBytes(charset);
            byte[] result = md.digest(data);
            return result;
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    /**
     * 检查文件的MD5值
     * 
     * @param file
     * @param offset
     * @param length
     * @return
     */
    public static String md5(File file, long offset, long length)
    {
        RandomAccessFile randomAccessFile = null;
        try
        {
            randomAccessFile = new RandomAccessFile(file, "r");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] src;
            if (length > 1024 * 1024)
            {
                src = new byte[1024 * 1024];
            }
            else
            {
                src = new byte[(int) length];
            }
            randomAccessFile.seek(offset);
            if (randomAccessFile.length() < offset + length)
            {
                throw new IllegalArgumentException();
            }
            long index = 0;
            for (; index + src.length < length; index += src.length)
            {
                randomAccessFile.read(src);
                md.update(src);
            }
            if (length - index > 0)
            {
                randomAccessFile.read(src, 0, (int) (length - index));
                md.update(src, 0, (int) (length - index));
            }
            return StringUtil.toHexString(md.digest());
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            if (randomAccessFile != null)
            {
                try
                {
                    randomAccessFile.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static String md5Str(String str)
    {
        return StringUtil.toHexString(md5(str));
    }
    
    public static void main(String[] args)
    {
        System.out.println(Md5Util.md5Str("wodexiaojing"));
    }
}
