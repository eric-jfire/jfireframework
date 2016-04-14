package com.jfireframework.jnet.common.result;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.jnet.common.channel.ServerChannelInfo;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class ServerInternalResult extends AbstractInternalResult
{
	private static Unsafe		unsafe		= ReflectUtil.getUnsafe();
	public static final boolean	UNDONE		= false;
	public static final boolean	DONE		= true;
	private volatile boolean	flowState	= UNDONE;
	public static final int		UNWRITED	= 0;
	public static final int		WRITED		= 1;
	private volatile int		writeState	= UNWRITED;
	private static long			_writeState	= ReflectUtil.getFieldOffset("writeState", ServerInternalResult.class);
	private ServerChannelInfo	channelInfo;
	private volatile long		cursor;
	private static final long	_cursor		= ReflectUtil.getFieldOffset("cursor", ServerInternalResult.class);
	
	public ServerInternalResult(long cursor, Object data, ServerChannelInfo channelInfo, int index)
	{
		init(cursor, data, channelInfo, index);
	}
	
	public void init(long cursor, Object data, ServerChannelInfo channelInfo, int index)
	{
		this.channelInfo = channelInfo;
		this.index = index;
		this.data = data;
		unsafe.putOrderedLong(this, _cursor, cursor);
		unsafe.putOrderedInt(this, _writeState, UNWRITED);
		flowState = UNDONE;
	}
	
	public long cursor()
	{
		return cursor;
	}
	
	public void flowDone()
	{
		flowState = DONE;
	}
	
	public boolean tryWrite()
	{
		if (flowState == UNDONE || writeState == WRITED || channelInfo.canWrite(this) == false)
		{
			return false;
		}
		if (unsafe.compareAndSwapInt(this, _writeState, UNWRITED, WRITED) == false)
		{
			return false;
		}
		return true;
	}
	
	public ServerChannelInfo getChannelInfo()
	{
		return channelInfo;
	}
	
}
