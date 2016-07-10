package com.jfireframework.rpc.client;

import java.nio.charset.Charset;
import java.util.concurrent.Future;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBufPool;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.client.AioClient;
import com.jfireframework.jnet.common.channel.ChannelInitListener;
import com.jfireframework.jnet.common.channel.JnetChannel;
import com.jfireframework.jnet.common.decodec.TotalLengthFieldBasedFrameDecoder;
import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.handler.LengthPreHandler;
import com.jfireframework.jnet.common.result.InternalTask;
import com.jfireframework.licp.Licp;

public class BytecodeInvoker
{
    protected int                    maxLength           = Integer.MAX_VALUE;
    protected long                   readTimeout         = 3000;
    protected long                   reuseChannelTimeout = 55000;
    protected String                 ip;
    protected int                    port;
    protected static Charset         charset             = Charset.forName("utf8");
    protected String                 proxyName;
    protected ThreadLocal<AioClient> clientChanelLocal;
    private static Logger            logger              = ConsoleLogFactory.getLogger();
    
    public BytecodeInvoker build()
    {
        clientChanelLocal = new ThreadLocal<AioClient>() {
            @Override
            protected AioClient initialValue()
            {
                AioClient client = new AioClient(false);
                client.setAddress(ip).setPort(port);
                client.setInitListener(new ChannelInitListener() {
                    
                    @Override
                    public void channelInit(JnetChannel channelInfo)
                    {
                        channelInfo.setReadTimeout(readTimeout);
                        channelInfo.setCapacity(1024);
                        channelInfo.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, maxLength));
                        channelInfo.setHandlers(new ReadHandler());
                    }
                });
                client.setWriteHandlers(new WriteHandler(proxyName), new LengthPreHandler(0, 4));
                return client;
            }
        };
        return this;
    }
    
    public Object invoke(String methodName, Object[] args) throws Throwable
    {
        AioClient client = clientChanelLocal.get().connect();
        Future<?> future = client.write(new Object[] { methodName, args });
        logger.debug("发送rpc调用数据");
        Object result = future.get();
        logger.debug("获得rpc调用结果成功");
        return result;
    }
    
    public BytecodeInvoker setProxyName(String proxyName)
    {
        this.proxyName = proxyName;
        return this;
    }
    
    public BytecodeInvoker setReadTimeout(final long readTimeout)
    {
        this.readTimeout = readTimeout;
        return this;
    }
    
    public BytecodeInvoker setReuseChannelTimeout(final long reuseChannelTimeout)
    {
        this.reuseChannelTimeout = reuseChannelTimeout;
        return this;
    }
    
    public BytecodeInvoker setIp(final String ip)
    {
        this.ip = ip;
        return this;
    }
    
    public BytecodeInvoker setPort(final int port)
    {
        this.port = port;
        return this;
    }
    
    public void close()
    {
        clientChanelLocal.get().close();
        clientChanelLocal.remove();
    }
    
}

class ReadHandler implements DataHandler
{
    protected ThreadLocal<Licp> lbseLocal = new ThreadLocal<Licp>() {
        @Override
        protected Licp initialValue()
        {
            return new Licp();
        }
    };
    
    @Override
    public Object handle(Object data, InternalTask result) throws JnetException
    {
        ByteBuf<?> buf = (ByteBuf<?>) data;
        Object tmp = lbseLocal.get().deserialize(buf);
        buf.release();
        return tmp;
    }
    
    @Override
    public Object catchException(Object data, InternalTask result)
    {
        return null;
    }
}

class WriteHandler implements DataHandler
{
    private String proxyName;
    
    public WriteHandler(String proxyName)
    {
        this.proxyName = proxyName;
    }
    
    protected ThreadLocal<Licp> lbseLocal = new ThreadLocal<Licp>() {
        @Override
        protected Licp initialValue()
        {
            return new Licp();
        }
    };
    
    /**
     * 准备需要发送的数据,将数据按照规定的格式填充到buffer中. 返回填充完毕的buffer
     * 
     * @param buffer
     * @param method
     * @param args
     */
    protected void prepareData(Licp lbse, String methodName, Object[] args, ByteBuf<?> buf)
    {
        int length = proxyName.length();
        buf.writePositive(length);
        for (int i = 0; i < length; i++)
        {
            buf.writeVarChar(proxyName.charAt(i));
        }
        // 写入方法名的长度
        length = methodName.length();
        buf.writePositive(length);
        for (int i = 0; i < length; i++)
        {
            buf.writeVarChar(methodName.charAt(i));
        }
        int argsNum = args == null ? 0 : args.length;
        // 写入参数个数
        buf.writePositive(argsNum);
        // 逐个写入参数
        for (int i = 0; i < argsNum; i++)
        {
            lbse.serialize(args[i], buf);
        }
    }
    
    @Override
    public Object handle(Object data, InternalTask result) throws JnetException
    {
        Object[] datas = (Object[]) data;
        String methodName = (String) datas[0];
        Object[] args = (Object[]) datas[1];
        Licp licp = lbseLocal.get();
        ByteBuf<?> buf = DirectByteBufPool.getInstance().get(100);
        buf.addWriteIndex(4);
        prepareData(licp, methodName, args, buf);
        return buf;
    }
    
    @Override
    public Object catchException(Object data, InternalTask result)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
