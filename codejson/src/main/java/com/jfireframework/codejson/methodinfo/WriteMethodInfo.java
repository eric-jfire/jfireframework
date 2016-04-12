package com.jfireframework.codejson.methodinfo;

/**
 * 方法信息，里面只包含一个方法。该方法用于返回进行json输出的动态编译代码
 * 代表用于get方法的信息输出
 * 
 */
public interface WriteMethodInfo
{
    /**
     * 
     * 返回该方法进行json输出时进行代码编译的文本字符串。（该动态代码中的json输出中，包含了逗号的输出）
     * 
     * @return
     */
    public String getOutput();
}
