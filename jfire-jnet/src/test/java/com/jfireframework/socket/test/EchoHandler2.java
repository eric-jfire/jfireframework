package com.jfireframework.socket.test;

import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.InternalTask;

public class EchoHandler2 implements DataHandler
{

	@Override
	public Object handle(Object data, InternalTask result) throws JnetException
	{
		return data;
	}

	@Override
	public Object catchException(Object data, InternalTask result)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
