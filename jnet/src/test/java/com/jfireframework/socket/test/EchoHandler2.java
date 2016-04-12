package com.jfireframework.socket.test;

import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.InternalResult;

public class EchoHandler2 implements DataHandler
{

	@Override
	public Object handle(Object data, InternalResult result) throws JnetException
	{
		return data;
	}

	@Override
	public Object catchException(Object data, InternalResult result)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
