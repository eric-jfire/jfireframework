package com.jfireframework.baseutil.encrypt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES 加解密工具类，注意，该类是非线程安全的
 * 
 * @author linbin
 *         
 */
public class DesUtil implements EnDecrpt
{
    private Cipher decryptCipher;
    private Cipher encrptCipher;
                   
    /**
     * 设置des加密使用的密钥
     * 
     * @param key
     */
    public void setKey(byte[] key)
    {
        try
        {
            SecretKey deskey = new SecretKeySpec(key, "DES"); // 加密
            encrptCipher = Cipher.getInstance("DES");
            encrptCipher.init(Cipher.ENCRYPT_MODE, deskey);
            decryptCipher = Cipher.getInstance("DES");
            decryptCipher.init(Cipher.DECRYPT_MODE, deskey);
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 加密原始信息
     * 
     * @param src
     * @return
     */
    public byte[] encrypt(byte[] src)
    {
        try
        {
            return encrptCipher.doFinal(src);
        }
        
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 解析加密信息
     * 
     * @param src
     * @return
     */
    public byte[] decrypt(byte[] src)
    {
        try
        {
            return decryptCipher.doFinal(src);
        }
        catch (IllegalBlockSizeException | BadPaddingException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void setPublicKey(byte[] publicKeyBytes)
    {
        throw new RuntimeException("DES为对称加密，无公钥");
    }
    
    @Override
    public void setPrivateKey(byte[] privateKeyBytes)
    {
        throw new RuntimeException("DES为对称加密，无私钥");
    }
    
    @Override
    public byte[] sign(byte[] src)
    {
        throw new RuntimeException("des无签名功能");
    }
    
    @Override
    public boolean check(byte[] src, byte[] sign)
    {
        throw new RuntimeException("des无签名功能");
    }
}
