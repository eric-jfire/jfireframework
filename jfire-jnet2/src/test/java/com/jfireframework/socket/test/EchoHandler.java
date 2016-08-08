package com.jfireframework.socket.test;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet2.common.exception.JnetException;
import com.jfireframework.jnet2.common.handler.DataHandler;
import com.jfireframework.jnet2.common.result.InternalResult;

public class EchoHandler implements DataHandler
{
    // private Logger logger =
    // ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
    private long time = System.currentTimeMillis();
    
    @Override
    public Object handle(Object data, InternalResult entry) throws JnetException
    {
        ByteBuf<?> byteBuf = (ByteBuf<?>) data;
//        try
//        {
//            Thread.sleep(10);
//        }
//        catch (InterruptedException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        byteBuf.readIndex(0);
        return byteBuf;
    }
    
    @Override
    public Object catchException(Object data, InternalResult result)
    {
        return null;
    }
    
}
