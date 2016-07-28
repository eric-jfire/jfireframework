package com.jfireframework.jnet.server.CompletionHandler.single;

import java.nio.channels.CompletionHandler;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public interface WeaponSingleWriteHandler extends CompletionHandler<Integer, ByteBuf<?>>
{
    public void write(ByteBuf<?> buf);
}
