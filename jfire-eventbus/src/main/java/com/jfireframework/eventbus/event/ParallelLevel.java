package com.jfireframework.eventbus.event;

public enum ParallelLevel
{
    /**
     * 并行处理
     */
    PAEALLEL, //
    /**
     * 串行处理。既事件最多只有一个线程在处理
     */
    SERIAL, //
    ROWKEY_SERIAL;
}
