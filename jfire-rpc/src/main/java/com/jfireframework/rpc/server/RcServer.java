package com.jfireframework.rpc.server;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.decodec.TotalLengthFieldBasedFrameDecoder;
import com.jfireframework.jnet.common.handler.LengthPreHandler;
import com.jfireframework.jnet.server.server.AioServer;
import com.jfireframework.jnet.server.server.ChannelInitListener;
import com.jfireframework.jnet.server.server.ServerChannelInfo;
import com.jfireframework.rpc.server.messagehandler.InvokeEntryHandler;
import sun.reflect.MethodAccessor;

@SuppressWarnings("restriction")
public class RcServer
{
	protected AioServer		serverMain;
	protected Logger		logger	= ConsoleLogFactory.getLogger();
	protected static Method	acquireMethodAccessor;
	protected static Field	methodAccessor;
	
	static
	{
		try
		{
			acquireMethodAccessor = Method.class.getDeclaredMethod("acquireMethodAccessor");
			acquireMethodAccessor.setAccessible(true);
			methodAccessor = Method.class.getDeclaredField("methodAccessor");
			methodAccessor.setAccessible(true);
		}
		catch (NoSuchMethodException | SecurityException | NoSuchFieldException e)
		{
			e.printStackTrace();
		}
	}
	
	protected RcServer()
	{
		
	}
	
	/**
	 * 以服务器配置文件类以及代理名称以及实例初始化远程调用服务器
	 * 
	 * @param port
	 * @param impl
	 */
	public RcServer(RcConfig rcConfig)
	{
		final InvokeEntryHandler handler = bind(rcConfig.getImplMap());
		rcConfig.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, rcConfig.getMaxLength()));
		rcConfig.setInitListener(new ChannelInitListener() {
			
			@Override
			public void channelInit(ServerChannelInfo serverChannelInfo)
			{
				serverChannelInfo.setHandlers(handler, new LengthPreHandler(0, 4));
			}
		});
		serverMain = new AioServer(rcConfig);
		
	}
	
	protected InvokeEntryHandler bind(Map<String, Object> implMap)
	{
		InvokeEntryHandler handler = new InvokeEntryHandler();
		handler.setWorkUnit(implMap, initMethodMap(implMap));
		return handler;
	}
	
	private Map<String, Map<String, MethodAccessor>> initMethodMap(Map<String, Object> implMap)
	{
		Map<String, Map<String, MethodAccessor>> methodsMap = new HashMap<>();
		for (Entry<String, Object> each : implMap.entrySet())
		{
			Map<String, MethodAccessor> tmp = new HashMap<>();
			Method[] methods = each.getValue().getClass().getDeclaredMethods();
			for (Method method : methods)
			{
				// 必须设置成true，这样调用效率最高。额外开销约等于直接调用。也就是总计调用耗时是原本的2倍
				method.setAccessible(true);
				tmp.put(method.getName(), fastMethod(method));
			}
			methodsMap.put(each.getKey(), tmp);
		}
		return methodsMap;
		
	}
	
	/**
	 * 获取method的更快的执行者MethodAccessor
	 * 
	 * @param src
	 * @return
	 */
	private MethodAccessor fastMethod(Method src)
	{
		try
		{
			acquireMethodAccessor.invoke(src);
			return (MethodAccessor) methodAccessor.get(src);
		}
		catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void start()
	{
		serverMain.start();
	}
	
	public void stop()
	{
		serverMain.stop();
	}
	
}
