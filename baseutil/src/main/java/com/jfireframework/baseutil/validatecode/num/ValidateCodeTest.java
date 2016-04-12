package com.jfireframework.baseutil.validatecode.num;

import java.io.IOException;
import java.util.Date;

public class ValidateCodeTest
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        ValidateCode vCode = new ValidateCode(120, 40, 5, 100);
        try
        {
            String path = "D:/t/" + new Date().getTime() + ".png";
            System.out.println(vCode.getCode() + " >" + path);
            vCode.write(path);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
}
