package com.jfireframework.fose.util;

import java.util.Comparator;
import com.jfireframework.fose.field.CacheField;

public class FieldComparator implements Comparator<CacheField>
{
	
	@Override
	public int compare(CacheField o1, CacheField o2)
	{
		if (o1.getOffset() > o2.getOffset())
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}
	
}
