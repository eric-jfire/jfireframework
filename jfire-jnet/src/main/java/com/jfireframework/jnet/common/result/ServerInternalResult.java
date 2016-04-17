package com.jfireframework.jnet.common.result;

import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.jnet.common.channel.ServerChannelInfo;
import com.jfireframework.jnet.server.CompletionHandler.ReadCompletionHandler;
import com.jfireframework.jnet.server.CompletionHandler.WriteCompletionHandler;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class ServerInternalResult extends AbstractInternalResult
{
    private static Unsafe          unsafe      = ReflectUtil.getUnsafe();
    public static final boolean    UNDONE      = false;
    public static final boolean    DONE        = true;
    private volatile boolean       flowState   = UNDONE;
    public static final int        UNWRITED    = 0;
    public static final int        WRITED      = 1;
    private volatile int           writeState  = UNWRITED;
    private static long            _writeState = ReflectUtil.getFieldOffset("writeState", ServerInternalResult.class);
    private long                   cursor;
    private static final long      _cursor     = ReflectUtil.getFieldOffset("cursor", ServerInternalResult.class);
    private ServerChannelInfo      channelInfo;
    private WriteCompletionHandler writeCompletionHandler;
    private ReadCompletionHandler  readCompletionHandler;
    
    public ServerInternalResult(long cursor, Object data, ServerChannelInfo channelInfo, ReadCompletionHandler readCompletionHandler, WriteCompletionHandler writeCompletionHandler, int index)
    {
        init(cursor, data, channelInfo, readCompletionHandler, writeCompletionHandler, index);
    }
    
    public void init(long cursor, Object data, ServerChannelInfo channelInfo, ReadCompletionHandler readCompletionHandler, WriteCompletionHandler writeCompletionHandler, int index)
    {
        this.channelInfo = channelInfo;
        this.readCompletionHandler = readCompletionHandler;
        this.writeCompletionHandler = writeCompletionHandler;
        this.index = index;
        this.data = data;
        unsafe.putOrderedLong(this, _cursor, cursor);
        unsafe.putOrderedInt(this, _writeState, UNWRITED);
        flowState = UNDONE;
    }
    
    public long cursor()
    {
        return cursor;
    }
    
    public void flowDone()
    {
        flowState = DONE;
    }
    
    public void write()
    {
        if (cursor != writeCompletionHandler.cursor() || writeState == WRITED)
        {
            return;
        }
        if (unsafe.compareAndSwapInt(this, _writeState, UNWRITED, WRITED) == false)
        {
            return;
        }
        channelInfo.socketChannel().write(((ByteBuf<?>) data).nioBuffer(), 10, TimeUnit.SECONDS, this, writeCompletionHandler);
    }
    
    public void write(long cursor)
    {
        if (cursor != writeCompletionHandler.cursor() || flowState == UNDONE || writeState == WRITED)
        {
            return;
        }
        if (unsafe.compareAndSwapInt(this, _writeState, UNWRITED, WRITED) == false)
        {
            return;
        }
        channelInfo.socketChannel().write(((ByteBuf<?>) data).nioBuffer(), 10, TimeUnit.SECONDS, this, writeCompletionHandler);
    }
    
    public boolean tryWrite()
    {
        if (flowState == UNDONE)
        {
            return false;
        }
        unsafe.putOrderedInt(this, _writeState, WRITED);
        return true;
    }
    
    public void directWrite()
    {
        channelInfo.socketChannel().write(((ByteBuf<?>) data).nioBuffer(), 10, TimeUnit.SECONDS, this, writeCompletionHandler);
    }
    
    public ServerChannelInfo getChannelInfo()
    {
        return channelInfo;
    }
    
    public ReadCompletionHandler getReadCompletionHandler()
    {
        return readCompletionHandler;
    }
}
