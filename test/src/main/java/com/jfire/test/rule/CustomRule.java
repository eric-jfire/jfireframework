package com.jfire.test.rule;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class CustomRule implements TestRule
{
    
    public Statement apply(Statement base, Description description)
    {
        MutiThreadTest test = description.getAnnotation(MutiThreadTest.class);
        if (test != null)
        {
            return new MutiThreadStatement(test.repeatTimes(), test.threadNums(), base);
        }
        RepeatTest repeatTest = description.getAnnotation(RepeatTest.class);
        if (repeatTest != null)
        {
            return new RepeatStatement(repeatTest.value(), base);
        }
        return base;
        
    }
    
}
