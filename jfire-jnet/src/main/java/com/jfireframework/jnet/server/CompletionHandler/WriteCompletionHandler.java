package com.jfireframework.jnet.server.CompletionHandler;

import java.nio.channels.CompletionHandler;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public interface WriteCompletionHandler extends CompletionHandler<Integer, ByteBuf<?>>
{
    public long cursor();
}
