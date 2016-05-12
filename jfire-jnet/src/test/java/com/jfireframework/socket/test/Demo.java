package com.jfireframework.socket.test;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;

public class Demo
{
public static void main(String[] args)
{
    ByteBuf<?> buf = DirectByteBuf.allocate(100);
    buf.writeString("123445654686213546133846163468461351686168666161631686156");
    System.out.println(buf.writeIndex());
}    
}
