package com.jfireframework.jnet.server.CompletionHandler;

import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet.common.channel.ServerChannelInfo;
import com.jfireframework.jnet.common.result.ServerInternalResult;

@Resource
public class WriteCompletionHandler implements CompletionHandler<Integer, ServerInternalResult>
{
    private volatile long         cursor = 0;
    private ReadCompletionHandler readCompletionHandler;
    // private static final Logger logger = ConsoleLogFactory.getLogger();
    
    public WriteCompletionHandler(ReadCompletionHandler readCompletionHandler)
    {
        this.readCompletionHandler = readCompletionHandler;
    }
    
    public long cursor()
    {
        return cursor;
    }
    
    @Override
    public void completed(Integer writeTotal, ServerInternalResult result)
    {
        try
        {
            ByteBuf<?> buf = (ByteBuf<?>) result.getData();
            buf.addReadIndex(writeTotal);
            if (buf.remainRead() > 0)
            {
                result.write();
                result.getChannelInfo().getChannel().write(buf.nioBuffer(), 10, TimeUnit.SECONDS, result, this);
                return;
            }
            else
            {
                ServerChannelInfo channelInfo = result.getChannelInfo();
                // logger.debug("当前写出{}", cursor);
                long nextCursor = cursor + 1;
                cursor = nextCursor;
                // 重启读取必须在更新了cursor之后，否则一重启读取又进入了等待读取状态。因为没有下一个可以容纳的空间
                // 由于上一步更新了cursor，所以下面的操作都存在并发的可能性，那么重启读取要保证只能被一个线程真正激发一次。否则就会造成多重读取异常。这依靠方法内的cas完成。
                readCompletionHandler.reStartRead();
                /**
                 * 这里必须进行可用性判断。如果不检测的话，会拿取到未正确初始化的数据导致进行了错误处理
                 * 具体错误是这样的：
                 * 拿到一个本来不可写的数据，但是读取线程已经准备重新初始化这个result了，但是只是初始化到flowState=
                 * undone前一步，这样可写判断就可以通过。
                 * 通过之后在获取bytebuffer写出前，读取线程就可能在处理这个数据，导致数据的紊乱或者别的问题
                 */
                if (readCompletionHandler.isAvailable(nextCursor))
                {
                    ServerInternalResult next = (ServerInternalResult) channelInfo.getResult(nextCursor);
                    if (next != null)
                    {
                        /**
                         * 这里必须使用带参数的写法。
                         * 否则的话，上面的cursor一前进，其他的线程就可以不停的写出。
                         * 导致拿到的result可能已经是下一轮的同一个位置的result。甚至可能是正在初始化中的result。
                         * 那样的话，就会出现将错误的数据写出的情况
                         */
                        next.write(nextCursor);
                    }
                }
                // 这一步最无关紧要，也不太可能引起一场，放到最后一步执行，这样也避免下面异常捕获的时候出现释放两次的情况
                buf.release();
            }
        }
        catch (Exception e)
        {
            ((ByteBuf<?>) result.getData()).release();
            result.getReadCompletionHandler().catchThrowable(e);
        }
    }
    
    @Override
    public void failed(Throwable exc, ServerInternalResult result)
    {
        ((ByteBuf<?>) result.getData()).release();
        result.getReadCompletionHandler().catchThrowable(exc);
    }
    
}
