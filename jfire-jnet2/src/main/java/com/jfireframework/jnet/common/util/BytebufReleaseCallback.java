package com.jfireframework.jnet.common.util;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.resource.ResourceCloseCallback;

public class BytebufReleaseCallback implements ResourceCloseCallback<ByteBuf<?>>
{
    public static final BytebufReleaseCallback instance = new BytebufReleaseCallback();
    
    private BytebufReleaseCallback()
    {
    }
    
    @Override
    public void onClose(ByteBuf<?> buf)
    {
        buf.release();
    }
    
}
