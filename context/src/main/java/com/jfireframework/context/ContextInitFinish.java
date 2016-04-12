package com.jfireframework.context;

import com.jfireframework.baseutil.order.Order;

/**
 * 容器初始化完成接口
 * 
 * @author windfire(windfire@zailanghua.com)
 *         
 */
public interface ContextInitFinish extends Order
{
    /**
     * 当容器初始化完成后，该接口会被容器调用
     * 
     * @author 林斌(eric@jfire.cn)
     */
    public void afterContextInit();
}
