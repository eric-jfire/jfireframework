package com.jfireframework.rpc.client;

import java.nio.charset.Charset;
import java.util.concurrent.Future;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBufPool;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.fose.Fose;
import com.jfireframework.jnet.client.AioClient;
import com.jfireframework.jnet.client.FutureClient;
import com.jfireframework.jnet.common.decodec.TotalLengthFieldBasedFrameDecoder;
import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.exception.SelfCloseException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.handler.LengthPreHandler;
import com.jfireframework.jnet.common.result.InternalResult;

public class BytecodeInvoker
{
	protected int						maxLength			= Integer.MAX_VALUE;
	protected long						readTimeout			= 3000;
	protected long						reuseChannelTimeout	= 55000;
	protected String					ip;
	protected int						port;
	protected static Charset			charset				= Charset.forName("utf8");
	protected String					proxyName;
	protected ThreadLocal<FutureClient>	clientChanelLocal;
	private static Logger				logger				= ConsoleLogFactory.getLogger();
	
	public BytecodeInvoker build()
	{
		clientChanelLocal = new ThreadLocal<FutureClient>() {
			@Override
			protected FutureClient initialValue()
			{
				FutureClient client = new FutureClient();
				client.setAddress(ip).setPort(port).setReadTimeout(readTimeout);
				client.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, maxLength));
				client.setWriteHandlers(new WriteHandler(proxyName), new LengthPreHandler(0, 4));
				client.setReadHandlers(new ReadHandler());
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
		clientChanelLocal.get().close(new SelfCloseException());
		clientChanelLocal.remove();
	}
	
}

class ReadHandler implements DataHandler
{
	protected ThreadLocal<Fose> lbseLocal = new ThreadLocal<Fose>() {
		@Override
		protected Fose initialValue()
		{
			return new Fose();
		}
	};
	
	@Override
	public Object handle(Object data, InternalResult result) throws JnetException
	{
		ByteBuf<?> buf = (ByteBuf<?>) data;
		Object tmp = lbseLocal.get().deserialize(buf);
		buf.release();
		return tmp;
	}
	
	@Override
	public Object catchException(Object data, InternalResult result)
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
	
	protected ThreadLocal<Fose> lbseLocal = new ThreadLocal<Fose>() {
		@Override
		protected Fose initialValue()
		{
			return new Fose();
		}
	};
	
	/**
	 * 准备需要发送的数据,将数据按照规定的格式填充到buffer中. 返回填充完毕的buffer
	 * 
	 * @param buffer
	 * @param method
	 * @param args
	 */
	protected void prepareData(Fose lbse, String methodName, Object[] args, ByteBuf<?> buf)
	{
		buf.writeString(proxyName);
		// 写入方法名的长度
		buf.writeString(methodName);
		int argsNum = args == null ? 0 : args.length;
		// 写入参数个数
		buf.writeInt(argsNum);
		// 逐个写入参数
		for (int i = 0; i < argsNum; i++)
		{
			lbse.serialize(args[i], buf);
		}
	}
	
	@Override
	public Object handle(Object data, InternalResult result) throws JnetException
	{
		Object[] datas = (Object[]) data;
		String methodName = (String) datas[0];
		Object[] args = (Object[]) datas[1];
		Fose fose = lbseLocal.get();
		ByteBuf<?> buf = DirectByteBufPool.getInstance().get(100);
		buf.addWriteIndex(4);
		prepareData(fose, methodName, args, buf);
		return buf;
	}
	
	@Override
	public Object catchException(Object data, InternalResult result)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
