package com.jfireframework.jnet.client;

import java.util.concurrent.Future;

public class AsyncClient extends AioClient
{
	
	@Override
	public Future<?> buildFuture()
	{
		return NORESULT;
	}
	
	
}
