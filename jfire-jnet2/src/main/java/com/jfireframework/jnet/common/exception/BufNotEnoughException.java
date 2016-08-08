package com.jfireframework.jnet.common.exception;

/**
 * 说明当前的buf不足以容纳一个tcp报文，需要增长大小。
 * 
 * @author 林斌
 * 
 */
public class BufNotEnoughException extends JnetException
{
	private int needSize;
	
	public BufNotEnoughException(int needSize)
	{
		this.needSize = needSize;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 当前一个完整的报文长度
	 * 
	 * @return
	 */
	public int getNeedSize()
	{
		return needSize;
	}
}
