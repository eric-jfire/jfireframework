package com.jfireframework.eventbus.event;

public enum ParallelLevel
{
    /**
     * 并行处理
     */
    PAEALLEL, //
    /**
     * 基于id的串行处理。意味着单个事件中，如果是同一个id，则最多只有一个线程在处理
     */
    ROWKEY_SERIAL, //
    /**
     * 事件串行处理。既单个事件最多只有一个线程在处理
     */
    EVENT_SERIAL, //
    /**
     * 事件大类基于id的串行处理。意味着在一个事件类型中，如果是同一个id，则最多只有一个线程在处理
     */
    TYPE_ROWKEY_SERIAL,
    /**
     * 事件大类穿行处理。即不同的事件，但是是一个类型，则最多只有一个线程在处理
     */
    TYPE_SERIAL, //
    RW_EVENT_READ, //
    RW_EVENT_WRITE;
}
