package com.jfireframework.baseutil.uniqueid;

public class ByteTool
{
    private static final char[] digits = new char[] { 'a', 'b', 'c', '0', '1', 'C', 'D', '2', '3', '4', 'N', 'O', 'P', 'Q', '5', 'G', 'H', '6', 'U', 'V', '7', 'o', 'p', 'q', '8', 'W', 'X', '9', //
            'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', //
            'A', 'B', 'E', 'F', 'I', 'J', 'K', 'L', 'M', 'R', 'S', 'T', 'Y', 'Z', //
            '-', '_' };
    
    public static final char toDigit(int i)
    {
        return digits[i];
    }
}
