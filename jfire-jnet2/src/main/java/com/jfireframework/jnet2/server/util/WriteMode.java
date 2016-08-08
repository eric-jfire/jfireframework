package com.jfireframework.jnet2.server.util;

public enum WriteMode
{
    // 单次写这种适合客户端要和服务器一问一答的形式。并且客户端需要根据服务器的反馈来进行下一次发出。这种情况下，是不会存在批量写的问题。因为没有批量的数据要处理
    SINGLE_WRITE,
    // 如果客户端可以持续不断的向服务端发送消息，则适合批量写出这种模式
    BATCH_WRITE;
}
