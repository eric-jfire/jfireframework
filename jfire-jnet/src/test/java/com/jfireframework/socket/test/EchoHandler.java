package com.jfireframework.socket.test;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.InternalTask;

public class EchoHandler implements DataHandler
{
    // private Logger logger =
    // ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
    private long time = System.currentTimeMillis();
    
    @Override
    public Object handle(Object data, InternalTask entry) throws JnetException
    {
        ByteBuf<?> byteBuf = (ByteBuf<?>) data;
        byteBuf.readIndex(0);
        return byteBuf;
    }
    
    @Override
    public Object catchException(Object data, InternalTask result)
    {
        Throwable e = (Throwable) data;
        e.printStackTrace();
        return data;
    }
    
}
