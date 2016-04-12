package com.jfireframework.sql.page;

import java.util.List;

public interface Page
{
    /**
     * 返回这个查询的页数
     * 
     * @return
     */
    public int getTotal();
    
    /**
     * 获得当前显示的页，页从1开始
     * 
     * @return
     */
    public int getPage();
    
    /**
     * 一页的大小
     * 
     * @return
     */
    public int getPageSize();
    
    /**
     * 返回当页的数据
     * 
     * @return
     */
    public List<?> getData();
    
    /**
     * 设置数据总量
     * 
     * @param total
     */
    public void setTotal(int total);
    
    /**
     * 设置当前的页数
     * 
     * @param page
     */
    public void setPage(int page);
    
    /**
     * mysql中查询分页的开始页数
     * 
     * @return
     */
    public int getStart();
    
    /**
     * 设置一页的大小
     * 
     * @param pageSize
     */
    public void setPageSize(int pageSize);
    
    /**
     * 设置当前页的数据
     * 
     * @param data
     */
    public void setData(List<?> data);
}
