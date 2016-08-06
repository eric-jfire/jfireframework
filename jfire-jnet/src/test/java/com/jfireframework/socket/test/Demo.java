package com.jfireframework.socket.test;

import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBuf;

public class Demo
{
    public static void main(String[] args)
    {
        ByteBuf<?> buf = HeapByteBuf.allocate(100);
       buf.writeString("你好，这里是客户端");
       System.out.println(buf.writeIndex());
    }
}
