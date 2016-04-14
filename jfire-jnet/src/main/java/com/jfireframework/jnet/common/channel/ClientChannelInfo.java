package com.jfireframework.jnet.common.channel;

import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import com.jfireframework.jnet.client.ClientReadCompleter;
import com.jfireframework.jnet.client.ResponseFuture;

@SuppressWarnings("restriction")
public class ClientChannelInfo extends AbstractChannelInfo
{
	private final Lock					lock		= new ReentrantLock();
	private final Future<?>[]			futures;
	private final int					futureSizeMask;
	private volatile long				writeCursor	= 0;
	private long						wrapPoint	= 0;
	private volatile Thread				writeThread;
	private final ClientReadCompleter	readCompleter;
	
	public ClientChannelInfo(int futureSize, ClientReadCompleter clientReadCompleter)
	{
		futures = new Future<?>[futureSize];
		futureSizeMask = futureSize - 1;
		readCompleter = clientReadCompleter;
	}
	
	public boolean isOpen()
	{
		return openState == OPEN;
	}
	
	public void signal(Object obj, long cursor)
	{
		ResponseFuture future = (ResponseFuture) getEntry(cursor);
		if (future != null)
		{
			future.setResult(obj);
			lock.lock();
			try
			{
				future.getHasResponse().signal();
			}
			finally
			{
				lock.unlock();
			}
		}
	}
	
	public void signalAll(Throwable e, long cursor)
	{
		if (writeThread != null)
		{
			LockSupport.unpark(writeThread);
		}
		lock.lock();
		ResponseFuture future;
		try
		{
			while (cursor < writeCursor)
			{
				future = (ResponseFuture) unsafe.getObjectVolatile(futures, base + ((cursor & futureSizeMask) << scale));
				if (future != null)
				{
					future.setE(e);
					future.getHasResponse().signal();
				}
				cursor += 1;
			}
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public Thread writeThrad()
	{
		return writeThread;
	}
	
	public void wakeWriteThread()
	{
		LockSupport.unpark(writeThread);
	}
	
	public Future<?> addFuture()
	{
		if (writeCursor >= wrapPoint)
		{
			wrapPoint = readCompleter.cursor() + futureSizeMask + 1;
			while (writeCursor >= wrapPoint)
			{
				writeThread = Thread.currentThread();
				LockSupport.park();
				wrapPoint = readCompleter.cursor() + futureSizeMask + 1;
			}
			writeThread = null;
		}
		ResponseFuture future = new ResponseFuture(lock, lock.newCondition());
		putEntry(future, writeCursor);
		return future;
	}
	
}
