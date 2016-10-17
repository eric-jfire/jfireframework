package com.jfireframework.eventbus;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PoolContext
{
    private Queue<String> queue = new LinkedList<String>();
    
    public PoolContext(int sum)
    {
        for (int i = 0; i < sum; i++)
        {
            queue.add(String.valueOf(i));
        }
    }
    
    public String poll()
    {
        return queue.poll();
    }
}
