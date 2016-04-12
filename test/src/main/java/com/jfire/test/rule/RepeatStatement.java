package com.jfire.test.rule;

import org.junit.runners.model.Statement;

public class RepeatStatement extends Statement
{
    private int       repaetTimes;
    private Statement statement;
    
    public RepeatStatement(int repaetTimes, Statement statement)
    {
        this.repaetTimes = repaetTimes;
        this.statement = statement;
    }
    
    @Override
    public void evaluate() throws Throwable
    {
        int i = 0;
        try
        {
            for (; i < repaetTimes; i++)
            {
                statement.evaluate();
            }
        }
        catch (Throwable e)
        {
            throw new RuntimeException("第" + i + "次测试发生错误", e);
        }
    }
    
}
