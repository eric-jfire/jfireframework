package com.jfireframework.fose;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBufPool;
import com.jfireframework.fose.Fose;

public class SimpleTest
{
	private int[][]				a		= new int[][] { { 1, 2 }, { 3, 4 } };
	private List<List<Integer>>	list	= new ArrayList<>();
	
	public SimpleTest()
	{
		List<Integer> tmp = new ArrayList<>();
		tmp.add(1);
		tmp.add(2);
		list.add(tmp);
		tmp = new ArrayList<>();
		tmp.add(3);
		tmp.add(4);
		list.add(tmp);
	}
	
	public int[][] getA()
	{
		return a;
	}
	
	public List<List<Integer>> getList()
	{
		return list;
	}
	
	@Test
	public void test()
	{
		Fose lbse = new Fose();
		Integer id = 100;
		ByteBuf<?> buf = HeapByteBufPool.getInstance().get(100);
		lbse.serialize(id, buf);
		System.out.println(lbse.deserialize(buf));
	}
}
