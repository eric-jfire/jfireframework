package com.jfireframework.context.event;

public interface EventPublisher
{
    public void publish(Object data, Enum<?> type);
}
