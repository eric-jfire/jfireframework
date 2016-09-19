package com.jfireframework.baseutil.test;

import java.nio.charset.Charset;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.encrypt.AesUtil;
import com.jfireframework.baseutil.encrypt.DesUtil;
import com.jfireframework.baseutil.encrypt.EnDecrpt;
import com.jfireframework.baseutil.encrypt.RSAUtil;

public class EnDecrptTest
{
    @Test
    public void testAes()
    {
        Random random = new Random();
        byte[] key = new byte[16];
        random.nextBytes(key);
        for (int i = 0; i < 1; i++)
        {
            AesUtil aesUtil = new AesUtil(key);
            String testStr = "测试的加密原文8iouhihiug ufytdf tred r";
            byte[] tmp = aesUtil.encrypt(testStr.getBytes());
            byte[] result = aesUtil.decrypt(tmp);
            Assert.assertEquals(testStr, new String(result));
            System.out.println(new String(result));
        }
    }
    
    @Test
    public void testDes()
    {
        byte[] key = "12345678".getBytes();
        EnDecrpt endecrpt = new DesUtil();
        endecrpt.setKey(key);
        String testStr = "测试的加密原文8iouhihiug ufytdf tred r";
        for (int i = 0; i < 100000; i++)
        {
            byte[] tmp = endecrpt.encrypt(testStr.getBytes());
            byte[] result = endecrpt.decrypt(tmp);
            Assert.assertEquals(testStr, new String(result));
        }
    }
    
    @Test
    public void testRsa()
    {
        RSAUtil rsaUtil = new RSAUtil();
        String publicKey = "30820122300d06092a864886f70d01010105000382010f003082010a02820101009237159d70fd01940c02e8c5c9361c12ca656ec06571b45c8e8dfe4381b7222cdce342b5c948bba668f9e15e43aece8fa729333c4b46c174c7fd01df9efe7dcc3fc004504fb21218d217721fe98b15c4867a538c7110c992aad2ae74f826d9f0e88c06b1b9d57c0cfaf45416a0e3edb105322bf44e659b186539366bfed2ee40ef707e1e639318ff006fca3634fadbe71df0c4abc7ce9b6ffaf35c7723115687b4e90a4d07bd2b5f57fb6bcb29e328f0edf9057e5f4a9bee9ceb5283f17ab2287e8d8f6a44716c785ec4351be4662855f85db56a8847d0ed80e010b73148cc4dec6ca398c1e9929a78b5ef98203f14e0561384e1d00d816fab3105fbb08863070203010001";
        String privateKey = "308204bc020100300d06092a864886f70d0101010500048204a6308204a202010002820101009237159d70fd01940c02e8c5c9361c12ca656ec06571b45c8e8dfe4381b7222cdce342b5c948bba668f9e15e43aece8fa729333c4b46c174c7fd01df9efe7dcc3fc004504fb21218d217721fe98b15c4867a538c7110c992aad2ae74f826d9f0e88c06b1b9d57c0cfaf45416a0e3edb105322bf44e659b186539366bfed2ee40ef707e1e639318ff006fca3634fadbe71df0c4abc7ce9b6ffaf35c7723115687b4e90a4d07bd2b5f57fb6bcb29e328f0edf9057e5f4a9bee9ceb5283f17ab2287e8d8f6a44716c785ec4351be4662855f85db56a8847d0ed80e010b73148cc4dec6ca398c1e9929a78b5ef98203f14e0561384e1d00d816fab3105fbb08863070203010001028201006312ce23a366dc45eba9a0fe3bebdd33e24ebeafc14d3d7025ec12e8bd215117e3085eb07cd63ac8748334f1419d563bc281c944c6a107fed070206fdcf5065a7aabea805cb005512a3256d346d54687a6b3869f3811aef421a9a62a7de71d6ffeb2baea7c482ac944d67f8fe1aeefe52de7241f636bb79edd1fda3f6d2891e508ba0111d072bc4620722e4214198cedf8fd764bcab7af5eeed88e2c5fe2662234bb5e56eaccf00ad66abd066a9499d3fc04f4784a808cd864d0376406349bf0ba0883adcc20e53888257357f8052525ed47919b2272f633aedbe427e0a7576f2ba40f910bccff2233fe5571cde2b9730e7da0e8ea3a9a703232aa4aff13199102818100d4ba82b2575667f79cd82380cea888afe646083d1dcbde13f89dbdc9d160f15306d9c0fe216c83f77ce3d02e2bed98b78d2649e6d07052b3af96954c82f56599f4af39d39e042103c634a6ca11b70acf4417d0f6b945db4acb8ae9eb6ac0e2b589882d4d35077ac1f95231e7e1fb073cf5f904b011860085ceb83f3c6ef2ebc902818100aff4ffbabdaa27fe4548e6320a234d0b55f8648e9e5e638d083f12c81a2fb1de7eff74a591560cfd519177f7df1b09d1a27db2c53741b033cf37b7b8a2d25406af20188093dc2f7e51b301c98ba1abb45619e9b7a7a7fbcfd1677766cca3f0d249c0246d824c7483bdf0e4165da298519fd3051e04201cdf10d43a2ad094a04f028180476ee5ea6b2789bbfd401b13ead16f2cd9ff944ea98dc6e21fef2046fff734b233afb57de0cd66b7198a0a6326bf1f342bc2bab8363031a5430626d64d7788099dbfa58469206bd1afa2088bfc4ce1e55db13dab4be5a850c26ec0441c51ef0f14c2d4ab836e228bb664227c02476053d92928a8d309936d5b504573ceb1e5f90281802b40107fe950e2e5b84813f526d2c88449cc75832ce04c9e04f1e3fef598d5a2b81ab8a45dd605f95a4c646db414645478196790dc653417780edd04f058bcb8abe13ac498139e44f1760bb16bb6008ba2425a557f4b66350de6ddcd91ed962b4a5f8554b99b6b645a92d4adbeb451b6768f9b0966c031f88e9d6bf2cc344c37028180762a8de24dbd1b32719e45d96c3ede26f189ea513e716c7f8264ad24b58ae8699408b99368d9e8bd7d63da334c01b5175dbaf041ac8031fc7f607ec407642f7d64e7fb953d13aa6abece7cf061e30f41511e77f78cdd0abff2eb8ebe7cc369d3a0dbfd7479ffff9393314d8f832c84d026b3adebcd467ddc7fff0900a51f19fd";
        rsaUtil.setPrivateKey(StringUtil.hexStringToBytes(privateKey));
        rsaUtil.setPublicKey(StringUtil.hexStringToBytes(publicKey));
        String content = "你好林斌,我是小静，我喜欢你很久了";
        byte[] tmp = rsaUtil.encrypt(content.getBytes());
        Assert.assertEquals(content, new String(rsaUtil.decrypt(tmp)));
        tmp = rsaUtil.sign(content.getBytes(Charset.forName("utf-8")));
        Assert.assertTrue(rsaUtil.check(content.getBytes(Charset.forName("utf-8")), tmp));
    }
}
