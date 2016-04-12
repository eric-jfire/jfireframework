package com.jfireframework.fose.util;

public class DimensionUtil
{
	public static int getDimByComponent(Class<?> target)
	{
		int dim = 0;
		while (target.isArray())
		{
			dim++;
			target = target.getComponentType();
		}
		return dim;
	}
}
