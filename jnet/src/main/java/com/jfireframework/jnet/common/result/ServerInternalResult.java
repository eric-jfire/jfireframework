package com.jfireframework.jnet.common.result;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.jnet.common.exception.SelfCloseException;
import com.jfireframework.jnet.server.server.ServerChannelInfo;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class ServerInternalResult extends AbstractInternalResult
{
    private static Unsafe       unsafe      = ReflectUtil.getUnsafe();
    public static final boolean UNDONE      = false;
    public static final boolean DONE        = true;
    public static final int     UNWRITED    = 0;
    public static final int     WRITED      = 1;
    private volatile boolean    flowState   = UNDONE;
    private volatile int        writeState  = UNWRITED;
    private static long         writeOffset = ReflectUtil.getFieldOffset("writeState", ServerInternalResult.class);
    private ServerChannelInfo   channelInfo;
                                
    public ServerInternalResult(Object data, ServerChannelInfo channelInfo, int index)
    {
        this.channelInfo = channelInfo;
        this.index = index;
        this.data = data;
        flowState = UNDONE;
        writeState = UNWRITED;
    }
    
    public void flowDone()
    {
        flowState = DONE;
    }
    
    public boolean tryWrite()
    {
        if (writeState == WRITED || channelInfo.isTopWriteResult(this) == false || flowState == UNDONE)
        {
            return false;
        }
        if (unsafe.compareAndSwapInt(this, writeOffset, UNWRITED, WRITED) == false)
        {
            return false;
        }
        return true;
    }
    
    public ServerChannelInfo getChannelInfo()
    {
        return channelInfo;
    }
    
    @Override
    public void closeChannel()
    {
        channelInfo.close(new SelfCloseException());
    }
    
    @Override
    public void closeChannel(Throwable e)
    {
        channelInfo.close(e);
    }
    
}
