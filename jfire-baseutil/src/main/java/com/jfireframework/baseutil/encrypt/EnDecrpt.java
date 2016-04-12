package com.jfireframework.baseutil.encrypt;

/**
 * 加解密接口，注意，该接口的实现，都是非线程安全的，必须在每一个线程中都新建一个对象才可以使用
 * 
 * @author linbin
 *         
 */
public interface EnDecrpt
{
    /**
     * 设置对称加密使用的密钥
     * 
     * @param key
     */
    public void setKey(byte[] key);
    
    /**
     * 设置非对称加密的公钥
     * 
     * @param publicKeyBytes
     */
    public void setPublicKey(byte[] publicKeyBytes);
    
    /**
     * 设置非对称加密的私钥
     * 
     * @param privateKeyBytes
     */
    public void setPrivateKey(byte[] privateKeyBytes);
    
    /**
     * 加密原始信息
     * 
     * @param src
     * @return
     */
    public byte[] encrypt(byte[] src);
    
    /**
     * 解析加密信息
     * 
     * @param src
     * @return
     */
    public byte[] decrypt(byte[] src);
    
    /**
     * 对内容进行签名，返回签名信息
     * 
     * @param src
     * @return
     */
    public byte[] sign(byte[] src);
    
    /**
     * 使用签名信息对原文进行验证，返回验证结果
     * 
     * @param src 原文内容
     * @param sign 签名内容
     * @return
     */
    public boolean check(byte[] src, byte[] sign);
}
