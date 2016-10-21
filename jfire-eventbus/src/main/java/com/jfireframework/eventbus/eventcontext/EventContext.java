package com.jfireframework.eventbus.eventcontext;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.executor.EventHandlerExecutor;
import com.jfireframework.eventbus.handler.EventHandler;

public interface EventContext<T>
{
    /**
     * 返回该事件绑定执行器
     * 
     * @return
     */
    public EventHandlerExecutor executor();
    
    /**
     * 返回该事件绑定的处理器
     * 
     * @return
     */
    public EventHandler<?, ?>[] combinationHandlers();
    
    /**
     * 等待直到该事件被处理完成
     */
    public void await();
    
    /**
     * 本事件处理过程中发生了异常，设置异常数据
     * 
     * @param e
     */
    public void setThrowable(Throwable e);
    
    /**
     * 本事件处理过程中捕获的异常
     * 
     * @return
     */
    public Throwable getThrowable();
    
    /**
     * 设置事件处理的结果数据
     * 
     * @param result
     */
    public void setResult(Object result);
    
    /**
     * 获取事件处理的结果数据,如果事件没有完成。就阻塞到完成为止
     * 
     * @return
     */
    public Object getResult();
    
    /**
     * 获取事件处理的结果数据。如果事件没有完成，就阻塞到事件完成为止或者超时退出
     * 如果超时时间到达还没有完成，抛出异常
     * 
     * @return
     */
    public Object getResult(long mills) throws InterruptedException;
    
    /**
     * 
     * 完成该事件，并且（如果有）唤醒等待该事件完成的线程
     */
    public void signal();
    
    /**
     * 等待该事件的完成，最多等待指定的毫秒数
     * 
     * @param mills
     */
    public void await(long mills);
    
    /**
     * 事件是否完成
     * 
     * @return
     */
    public boolean isFinished();
    
    /**
     * 返回事件的待处理数据
     * 
     * @return
     */
    public Object getEventData();
    
    /**
     * 该事件上下文的事件类型
     * 
     * @return
     */
    public Enum<? extends EventConfig> getEvent();
    
}
