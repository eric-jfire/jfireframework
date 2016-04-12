package com.jfireframework.job.test;

import com.jfireframework.job.Job;

public class NoNextRoundJob implements Job
{
    
    @Override
    public void doJob()
    {
        System.out.println("hah");
    }
    
    @Override
    public boolean nextRound()
    {
        return false;
    }
    
}
