package com.jfire.test.rule;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.runners.model.Statement;

public class MutiThreadStatement extends Statement
{
    private int             repaetTimes;
    private int             threadNums;
    private Statement       statement;
    private ExecutorService pool;
    
    public MutiThreadStatement(int repaetTimes, int threadNums, Statement statement)
    {
        this.repaetTimes = repaetTimes;
        this.threadNums = threadNums;
        this.statement = statement;
        pool = Executors.newFixedThreadPool(threadNums);
    }
    
    public void evaluate() throws Throwable
    {
        List<Future<Void>> set = new LinkedList<Future<Void>>();
        for (int i = 0; i < threadNums; i++)
        {
            Future<Void> future = pool.submit(new Callable<Void>() {
                
                public Void call() throws Exception
                {
                    try
                    {
                        for (int j = 0; j < repaetTimes; j++)
                        {
                            statement.evaluate();
                        }
                        return null;
                    }
                    catch (Throwable e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            });
            set.add(future);
        }
        pool.shutdown();
        for (Future<Void> each : set)
        {
            each.get();
        }
    }
    
}
