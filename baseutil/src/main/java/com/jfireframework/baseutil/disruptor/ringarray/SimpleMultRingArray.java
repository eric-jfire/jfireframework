package com.jfireframework.baseutil.disruptor.ringarray;

import com.jfireframework.baseutil.disruptor.CpuCachePadingValue;
import com.jfireframework.baseutil.disruptor.EntryAction;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategy;

public class SimpleMultRingArray extends AbstractRingArray
{
	private CpuCachePadingValue	preAdd	= new CpuCachePadingValue();
	
	public SimpleMultRingArray(int size, WaitStrategy waitStrategy, EntryAction[] actions)
	{
		super(size, waitStrategy, actions);
	}
	
	@Override
	public void publish(long cursor)
	{
		add.casSet(cursor - 1, cursor);
		waitStrategy.signallBlockwaiting();
	}
	
	@Override
	public boolean isAvailable(long cursor)
	{
		if (cursor <= add.getPoint())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	protected long getNext()
	{
		return preAdd.next();
	}
	
}
