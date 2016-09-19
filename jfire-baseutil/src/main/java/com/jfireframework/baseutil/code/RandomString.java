package com.jfireframework.baseutil.code;

import java.util.Random;

public class RandomString
{
    private static Random random         = new Random();
    private static char[] numbers        = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
    private static char[] charAndNumbers = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
    
    /**
     * 得到一个随机的数字串，位数由参数指定
     * 
     * @param size
     * @return
     */
    public static String getNumber(int size)
    {
        char[] tmp = new char[size];
        for (int i = 0; i < size; i++)
        {
            tmp[i] = numbers[random.nextInt(10)];
        }
        return new String(tmp);
    }
    
    /**
     * 返回一个长度为size的随机字符串，可能包含数字和字母
     * 
     * @param size
     * @return
     */
    public static String randomString(int size)
    {
        char[] tmp = new char[size];
        for (int i = 0; i < size; i++)
        {
            tmp[i] = charAndNumbers[random.nextInt(62)];
        }
        return new String(tmp);
    }
    
}
