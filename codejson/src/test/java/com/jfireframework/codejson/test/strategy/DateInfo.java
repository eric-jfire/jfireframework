package com.jfireframework.codejson.test.strategy;

import java.util.Date;

public class DateInfo
{
    private Date     date;
    
    private Date[]   dates;
    private NestInfo nestInfo;
    private double   d = 2.3569;
    
    public double getD()
    {
        return d;
    }
    
    public void setD(double d)
    {
        this.d = d;
    }
    
    public Date getDate()
    {
        return date;
    }
    
    public NestInfo getNestInfo()
    {
        return nestInfo;
    }
    
    public void setNestInfo(NestInfo nestInfo)
    {
        this.nestInfo = nestInfo;
    }
    
    public void setDate(Date date)
    {
        this.date = date;
    }
    
    public Date[] getDates()
    {
        return dates;
    }
    
    public void setDates(Date[] dates)
    {
        this.dates = dates;
    }
    
}
