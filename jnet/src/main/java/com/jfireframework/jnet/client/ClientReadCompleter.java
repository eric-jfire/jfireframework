package com.jfireframework.jnet.client;

import java.nio.channels.CompletionHandler;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.exception.BufNotEnoughException;
import com.jfireframework.jnet.common.exception.EndOfStreamException;
import com.jfireframework.jnet.common.exception.LessThanProtocolException;
import com.jfireframework.jnet.common.exception.NotFitProtocolException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.ClientInternalResult;

public class ClientReadCompleter implements CompletionHandler<Integer, ClientChannelInfo>
{
    private DataHandler[] handlers;
    private FrameDecodec  frameDecodec;
                          
    public ClientReadCompleter(FrameDecodec frameDecodec, DataHandler... handlers)
    {
        if (handlers == null || handlers.length == 0)
        {
            throw new RuntimeException("参数不能为空");
        }
        this.handlers = handlers;
        this.frameDecodec = frameDecodec;
    }
    
    @Override
    public void completed(Integer result, ClientChannelInfo channelInfo)
    {
        if (result == -1)
        {
            // 调用一个具体的方法将所有的future销毁
            channelInfo.close(new EndOfStreamException());
            return;
        }
        ByteBuf<?> ioBuf = channelInfo.ioBuf();
        ioBuf.addWriteIndex(result);
        Object decodeResult = null;
        ByteBuf<?> buf = null;
        do
        {
            try
            {
                decodeResult = buf = frameDecodec.decodec(ioBuf);
                if (decodeResult != null)
                {
                    buf.maskRead();
                    buf.resetRead();
                    ClientInternalResult internalResult = new ClientInternalResult(decodeResult, channelInfo, 0);
                    for (int i = 0; i < handlers.length;)
                    {
                        decodeResult = handlers[i].handle(decodeResult, internalResult);
                        if (i == internalResult.getIndex())
                        {
                            i++;
                            internalResult.setIndex(i);
                        }
                        else
                        {
                            i = internalResult.getIndex();
                        }
                    }
                    if (decodeResult != buf)
                    {
                        buf.release();
                    }
                    channelInfo.popOneFuture(decodeResult);
                }
                if (ioBuf.remainRead() == 0)
                {
                    channelInfo.readAndWait();
                    return;
                }
            }
            catch (NotFitProtocolException e)
            {
                channelInfo.close(e);
                return;
            }
            catch (BufNotEnoughException e)
            {
                ioBuf.compact();
                ioBuf.ensureCapacity(e.getNeedSize());
                channelInfo.continueRead();
                return;
            }
            catch (LessThanProtocolException e)
            {
                channelInfo.readAndWait();
                return;
            }
            catch (Exception e)
            {
                channelInfo.close(e);
                return;
            }
        } while (decodeResult != null);
        channelInfo.readAndWait();
    }
    
    @Override
    public void failed(Throwable exc, ClientChannelInfo channelInfo)
    {
        // 调用一个具体的方法将所有的future销毁
        channelInfo.close(exc);
    }
    
}
