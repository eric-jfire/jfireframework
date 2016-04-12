package com.jfireframework.fose.serializer;

import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;

public interface Serializer
{
	
	/**
	 * 获取该实例的所有对象属性的实例集合，可能包含该对象自身。根据实现类不同的规则判断
	 * 
	 * @param src
	 */
	public void getObjects(Object src, ObjectCollect collect);
	
	/**
	 * 将对象src序列化到cache中。
	 * 
	 * @param src
	 * @param cache
	 * @param collect
	 * @param register TODO
	 */
	public void serialize(Object src, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register);
	
	/**
	 * 反序列化二进制字节到对象中去
	 * 
	 * @param target
	 * @param cache
	 * @param collect
	 * @param register TODO
	 */
	public void deserialize(Object target, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register);
	
}
