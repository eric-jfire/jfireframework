package com.jfireframework.context.event;

public interface EventPublisher
{
    public ApplicationEvent publish(Object data, Enum<?> type);
}
