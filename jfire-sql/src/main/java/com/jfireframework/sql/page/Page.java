package com.jfireframework.sql.page;

import java.util.List;

public class Page
{
    protected int     total;
    protected int     page;
    protected int     pageSize;
    protected List<?> data;
    
    public int getTotal()
    {
        return total;
    }
    
    public int getPage()
    {
        return page;
    }
    
    public int getStart()
    {
        return (page - 1) * pageSize;
    }
    
    public int getPageSize()
    {
        return pageSize;
    }
    
    public List<?> getData()
    {
        return data;
    }
    
    public void setTotal(int total)
    {
        this.total = total;
    }
    
    public void setPage(int page)
    {
        this.page = page;
    }
    
    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }
    
    public void setData(List<?> data)
    {
        this.data = data;
    }
    
}
