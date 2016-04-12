package com.jfireframework.baseutil.time;

/**
 * 时间观察类
 * 
 * @author 林斌（windfire@zailanghua.com）
 *         
 */
public class Timewatch
{
    private long t0 = System.currentTimeMillis();
    private long t1 = System.currentTimeMillis();
                    
    /**
     * 开始计时
     */
    public void start()
    {
        t0 = System.currentTimeMillis();
    }
    
    /**
     * 结束计时
     */
    public void end()
    {
        t1 = System.currentTimeMillis();
    }
    
    /**
     * 返回统计时间
     * 
     * @return
     */
    public long getTotal()
    {
        return t1 - t0;
    }
}
