package com.jfireframework.jnet.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ResponseFuture implements Future<Object>
{
	private static final Object	NORESULT	= new Object();
	protected Object			result		= NORESULT;
	protected final Lock		lock;
	protected final Condition	hasResponse;
	protected Throwable			e;
	
	public ResponseFuture(Lock lock, Condition hasResponse)
	{
		this.lock = lock;
		this.hasResponse = hasResponse;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning)
	{
		return false;
	}
	
	@Override
	public boolean isCancelled()
	{
		return false;
	}
	
	@Override
	public boolean isDone()
	{
		return result != NORESULT;
	}
	
	@Override
	public Object get() throws InterruptedException, ExecutionException
	{
		lock.lock();
		try
		{
			while (result == NORESULT && e == null)
			{
				hasResponse.await();
			}
			if (e != null)
			{
				throw new ExecutionException(e);
			}
			return result;
		}
		finally
		{
			lock.unlock();
		}
		
	}
	
	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
	{
		lock.lock();
		try
		{
			while (result == NORESULT)
			{
				hasResponse.await(timeout, unit);
				if (result == NORESULT)
				{
					if (e != null)
					{
						throw new ExecutionException(e);
					}
					throw new TimeoutException("等待时间已到达");
				}
				else
				{
					return result;
				}
			}
			return result;
		}
		finally
		{
			lock.unlock();
		}
	}
	
}
