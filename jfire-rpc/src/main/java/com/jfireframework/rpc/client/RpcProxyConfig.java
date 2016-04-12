package com.jfireframework.rpc.client;

public class RpcProxyConfig<T>
{
	private String		proxyName;
	private String		ip;
	private int			port;
	private long		readTimeout			= 3000;
	private long		reuseChannelTimeout	= 55000;
	private Class<T>	interfaceClass;
	
	public RpcProxyConfig(Class<T> interfaceClass)
	{
		this.interfaceClass = interfaceClass;
	}
	
	public T getProxy()
	{
		checkProxyParams();
		BytecodeInvoker bytecodeInvoker = new BytecodeInvoker();
		bytecodeInvoker.setIp(ip).setPort(port).setProxyName(proxyName).setReadTimeout(readTimeout).setReuseChannelTimeout(reuseChannelTimeout);
		return RpcFactory.getProxy(interfaceClass, bytecodeInvoker);
	}
	
	private void checkProxyParams()
	{
		if (ip == null)
		{
			throw new RuntimeException("请设置服务端ip");
		}
		if (port < 0 || port > 62255)
		{
			throw new RuntimeException("请设置服务端端口");
		}
		
	}
	
	public RpcProxyConfig<T> setProxyName(String proxyName)
	{
		this.proxyName = proxyName;
		return this;
	}
	
	public RpcProxyConfig<T> setIp(String ip)
	{
		this.ip = ip;
		return this;
	}
	
	public RpcProxyConfig<T> setPort(int port)
	{
		this.port = port;
		return this;
	}
	
	public RpcProxyConfig<T> setReadTimeout(long readTimeout)
	{
		this.readTimeout = readTimeout;
		return this;
	}
	
	public RpcProxyConfig<T> setReuseChannelTimeout(long reuseChannelTimeout)
	{
		this.reuseChannelTimeout = reuseChannelTimeout;
		return this;
	}
	
}
