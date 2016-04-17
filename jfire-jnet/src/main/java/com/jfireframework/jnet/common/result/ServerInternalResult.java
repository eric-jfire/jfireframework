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
    
    private static final class WriteState
    {
        public static final int UNWRITED = 0;
        public static final int WRITED   = 1;
        private final long      version;
        private final int       state;
        
        private WriteState(int state, long version)
        {
            this.version = version;
            this.state = state;
        }
        
        static WriteState of(int state, long version)
        {
            return new WriteState(state, version);
        }
    }
    
    private static Unsafe          unsafe      = ReflectUtil.getUnsafe();
    public static final boolean    UNDONE      = false;
    public static final boolean    DONE        = true;
    private volatile boolean       flowState   = UNDONE;
    private volatile WriteState    writeState;
    private static long            _writeState = ReflectUtil.getFieldOffset("writeState", ServerInternalResult.class);
    private ServerChannelInfo      channelInfo;
    private WriteCompletionHandler writeCompletionHandler;
    private ReadCompletionHandler  readCompletionHandler;
    
    public ServerInternalResult(long version, Object data, ServerChannelInfo channelInfo, ReadCompletionHandler readCompletionHandler, WriteCompletionHandler writeCompletionHandler, int index)
    {
        init(version, data, channelInfo, readCompletionHandler, writeCompletionHandler, index);
    }
    
    public void init(long version, Object data, ServerChannelInfo channelInfo, ReadCompletionHandler readCompletionHandler, WriteCompletionHandler writeCompletionHandler, int index)
    {
        this.channelInfo = channelInfo;
        this.readCompletionHandler = readCompletionHandler;
        this.writeCompletionHandler = writeCompletionHandler;
        this.index = index;
        this.data = data;
        unsafe.putOrderedObject(this, _writeState, WriteState.of(WriteState.UNWRITED, version));
        flowState = UNDONE;
    }
    
    public long version()
    {
        return writeState.version;
    }
    
    public void flowDone()
    {
        flowState = DONE;
    }
    
    public void write(long version)
    {
        if (version != writeCompletionHandler.cursor() || flowState == UNDONE || writeState.state == WriteState.WRITED)
        {
            return;
        }
        if (casState(version) == false)
        {
            return;
        }
        channelInfo.socketChannel().write(((ByteBuf<?>) data).nioBuffer(), 10, TimeUnit.SECONDS, this, writeCompletionHandler);
    }
    
    private boolean casState(long expectedVersion)
    {
        WriteState current = writeState;
        return current.version == expectedVersion && current.state == WriteState.UNWRITED && unsafe.compareAndSwapObject(this, _writeState, current, WriteState.of(WriteState.WRITED, expectedVersion));
    }
    
    public boolean tryWrite(long version)
    {
        if (flowState == UNDONE)
        {
            return false;
        }
        unsafe.putOrderedObject(this, _writeState, WriteState.of(WriteState.WRITED, version));
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
