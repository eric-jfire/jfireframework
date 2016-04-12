package com.jfireframework.baseutil.collection.buffer;

import java.nio.ByteBuffer;
import java.util.Queue;

public class DirectByteBufPool extends ByteBufPool<ByteBuffer>
{
	
	private static volatile DirectByteBufPool INSTANCE;
	
	private DirectByteBufPool()
	{
	}
	
	public static DirectByteBufPool getInstance()
	{
		if (INSTANCE == null)
		{
			synchronized (HeapByteBufPool.class)
			{
				if (INSTANCE == null)
				{
					INSTANCE = new DirectByteBufPool();
				}
			}
		}
		return INSTANCE;
	}
	
	@Override
	protected void expend(ByteBuf<ByteBuffer> buf, int need)
	{
		ByteBuffer tmp = null;
		Queue<ByteBuffer> host = null;
		for (CacheSize each : sizes)
		{
			if (each.biggerThan(need))
			{
				ByteBuffer buffer = arrays[each.index()].poll();
				if (buffer != null)
				{
					tmp = buffer;
					tmp.clear();
				}
				else
				{
					tmp = ByteBuffer.allocateDirect(each.size());
				}
				host = arrays[each.index()];
				break;
			}
		}
		if (tmp == null)
		{
			tmp = ByteBuffer.allocateDirect(need);
		}
		((DirectByteBuf) buf).changeToReadState();
		ByteBuffer src = buf.memory;
		tmp.put(src);
		buf.release();
		buf.host = host;
		buf.memory = tmp;
		buf.readIndex(0);
		buf.writeIndex(tmp.position());
	}
	
	@Override
	public DirectByteBuf get(int size)
	{
		for (CacheSize each : sizes)
		{
			if (each.biggerThan(size))
			{
				ByteBuffer buffer = arrays[each.index()].poll();
				if (buffer != null)
				{
					buffer.clear();
					return new DirectByteBuf(buffer, arrays[each.index()]);
				}
				else
				{
					return new DirectByteBuf(ByteBuffer.allocateDirect(each.size()), arrays[each.index()]);
				}
			}
		}
		return new DirectByteBuf(ByteBuffer.allocateDirect(size), null);
	}
	
}
