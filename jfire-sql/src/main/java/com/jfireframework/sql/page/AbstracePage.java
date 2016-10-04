package com.jfireframework.sql.page;

import java.util.List;

public abstract class AbstracePage implements Page
{
    protected int     total;
    protected int     page;
    protected int     pageSize;
    protected List<?> data;
    
    @Override
    public int getTotal()
    {
        return total;
    }
    
    @Override
    public int getPage()
    {
        return page;
    }
    
    public int getStart()
    {
        return (page - 1) * pageSize;
    }
    
    @Override
    public int getPageSize()
    {
        return pageSize;
    }
    
    @Override
    public List<?> getData()
    {
        return data;
    }
    
    @Override
    public void setTotal(int total)
    {
        this.total = total;
    }
    
    @Override
    public void setPage(int page)
    {
        this.page = page;
    }
    
    @Override
    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }
    
    @Override
    public void setData(List<?> data)
    {
        this.data = data;
    }
    
}
