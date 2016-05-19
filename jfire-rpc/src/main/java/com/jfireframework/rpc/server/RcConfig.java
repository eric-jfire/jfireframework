package com.jfireframework.rpc.server;

import java.util.Map;
import com.jfireframework.jnet.server.util.ServerConfig;

public class RcConfig extends ServerConfig
{
	private Map<String, Object>	implMap;
	private int					maxLength	= Integer.MAX_VALUE;
	
	public int getMaxLength()
	{
		return maxLength;
	}
	
	public void setMaxLength(int maxLength)
	{
		this.maxLength = maxLength;
	}
	
	public Map<String, Object> getImplMap()
	{
		return implMap;
	}
	
	public void setImplMap(Map<String, Object> implMap)
	{
		this.implMap = implMap;
	}
	
}
