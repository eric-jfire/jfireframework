package com.jfireframework.jnet.common.result;

import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.concurrent.UnsafeReferenceFieldUpdater;
import com.jfireframework.jnet.common.channel.ServerChannelInfo;
import com.jfireframework.jnet.server.CompletionHandler.ReadCompletionHandler;
import com.jfireframework.jnet.server.CompletionHandler.WriteCompletionHandler;

public class ServerInternalResult extends AbstractInternalResult
{
    /**
     * 写出许可。持有写出许可的线程才可以对数据进行写出操作
     *
     */
    private static final class WritePermission
    {
        public static final int UN_take = 0;
        public static final int TAKED   = 1;
        private final long      version;
        private final int       state;
        
        private WritePermission(int state, long version)
        {
            this.version = version;
            this.state = state;
        }
        
        static WritePermission valueOf(int state, long version)
        {
            return new WritePermission(state, version);
        }
    }
    
    public static final boolean                                                UNDONE    = false;
    public static final boolean                                                DONE      = true;
    private volatile boolean                                                   flowState = UNDONE;
    private volatile WritePermission                                           writePermission;
    private UnsafeReferenceFieldUpdater<ServerInternalResult, WritePermission> updater   = new UnsafeReferenceFieldUpdater<>(ServerInternalResult.class, "writePermission");
    private ServerChannelInfo                                                  channelInfo;
    private WriteCompletionHandler                                             writeCompletionHandler;
    private ReadCompletionHandler                                              readCompletionHandler;
    
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
        updater.orderSet(this, WritePermission.valueOf(WritePermission.UN_take, version));
        flowState = UNDONE;
    }
    
    public long version()
    {
        return writePermission.version;
    }
    
    public void flowDone()
    {
        flowState = DONE;
    }
    /**
     * 确认当前的版本是否等于写出完成器的序号，流程是否处理完毕，写出许可是否处于未获得状态。
     * 如果都满足，则cas方式获得写出许可。注意，这里使用的是带版本号的cas。
     * 
     * 这个方法的并发比较复杂，下面会分点来进行详细讲述。
     * 为什么采用writePermission这样的形式？
     * 这个方法可能会被两个线程同时调用。第一个是异步模式下的处理器线程或者是同步模式下的读取线程，第二个就是写出线程。假设两个线程同时通过了第一个if判断。
     * 此时线程2失去了cpu时间。则线程1不断的往下执行。在执行完毕后，成功写出，就会增加WriteCompletionHandler这个类里的序号。
     * 由于整个Channel中，result对象是由数组存储，并且是循环复用的。那么会出现一个情况就是线程2在很久之后（这里的很久，在cpu时间中可能也就只有毫秒级别），重新获得时间片。
     * 此时进行获取写出许可操作。由于该result最早的时候被线程1写出后又重新被复用，将写出许可，流程状态都重置为初始值。那么此时线程2就会错误的取得一个非处理完毕的数据，并且尝试写出。
     * 导致发生错误。这个问题的根源其实就是ABA问题，在许可被更改，数据被写出后，许可恢复初始值，导致线程2错误的以为可以更新。
     * 解决这个问题，就是为许可字段本身添加一个版本号。这样线程2在很久以后尝试更新，就会因为版本号错误，而无法取得许可。保证了正确性。
     * 
     * @param version 当前期望写出的版本号
     */
    public void write(long version)
    {
        if (version != writeCompletionHandler.cursor() || flowState == UNDONE || writePermission.state == WritePermission.TAKED)
        {
            return;
        }
        if (casState(version) == false)
        {
            return;
        }
        channelInfo.socketChannel().write(((ByteBuf<?>) data).nioBuffer(), 10, TimeUnit.SECONDS, (ByteBuf<?>) data, writeCompletionHandler);
    }
    
    private boolean casState(long expectedVersion)
    {
        WritePermission current = writePermission;
        return current.version == expectedVersion && current.state == WritePermission.UN_take && updater.compareAndSwap(this, current, WritePermission.valueOf(WritePermission.TAKED, expectedVersion));
    }
    
    /**
     * 如果该数据已经被处理器处理完毕，也就是状态为DONE。则获得该结果的写出许可
     * 
     * @param version
     * @return
     */
    public boolean tryWrite(long version)
    {
        if (flowState == UNDONE)
        {
            return false;
        }
        updater.orderSet(this, WritePermission.valueOf(WritePermission.TAKED, version));
        return true;
    }
    
    public void directWrite()
    {
        channelInfo.socketChannel().write(((ByteBuf<?>) data).nioBuffer(), 10, TimeUnit.SECONDS, (ByteBuf<?>) data, writeCompletionHandler);
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
