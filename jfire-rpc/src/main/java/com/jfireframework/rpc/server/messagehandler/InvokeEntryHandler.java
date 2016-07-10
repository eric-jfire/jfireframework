package com.jfireframework.rpc.server.messagehandler;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import javax.annotation.Resource;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.InternalTask;
import com.jfireframework.licp.Licp;
import com.jfireframework.rpc.exception.NoSuchMethodException;
import com.jfireframework.rpc.exception.NoSuchProxyException;
import sun.reflect.MethodAccessor;

@SuppressWarnings("restriction")
@Resource
public class InvokeEntryHandler implements DataHandler
{
    private Map<String, Object>                      implMap;
    private Map<String, Map<String, MethodAccessor>> methodMaps;
    private ThreadLocal<Licp>                        threadLocalLbse = new ThreadLocal<Licp>() {
                                                                         protected Licp initialValue()
                                                                         {
                                                                             return new Licp();
                                                                         }
                                                                     };
    private Logger                                   logger          = ConsoleLogFactory.getLogger();
    
    private Object invoke(String proxyName, String methodName, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Object impl = implMap.get(proxyName);
        if (impl != null)
        {
            MethodAccessor method = methodMaps.get(proxyName).get(methodName);
            if (method == null)
            {
                throw new NoSuchMethodException(methodName);
            }
            logger.debug("正确解析rpc数据，进行方法调用{}", args[0]);
            return method.invoke(impl, args);
        }
        throw new NoSuchProxyException(proxyName);
        
    }
    
    /**
     * 设置必须的工作单元，包含代理名称数组，代理实例，以及各实例方法名和方法访问映射数组
     * 
     * @param proxyNames
     * @param impls
     * @param methodMaps
     */
    public void setWorkUnit(Map<String, Object> implMap, Map<String, Map<String, MethodAccessor>> methodMaps)
    {
        this.implMap = implMap;
        this.methodMaps = methodMaps;
    }
    
    @Override
    public Object handle(Object data, InternalTask result) throws JnetException
    {
        String proxyName = null;
        String methodName = null;
        int argsNum = -1;
        Object[] args;
        ByteBuf<?> buf = (ByteBuf<?>) data;
        try
        {
            int length = buf.readPositive();
            char[] src = new char[length];
            for (int i = 0; i < length; i++)
            {
                src[i] = buf.readVarChar();
            }
            proxyName = new String(src);
            length = buf.readPositive();
            src = new char[length];
            for (int i = 0; i < length; i++)
            {
                src[i] = buf.readVarChar();
            }
            methodName = new String(src);
            argsNum = buf.readPositive();
            args = new Object[argsNum];
            Licp lbse = threadLocalLbse.get();
            for (int i = 0; i < argsNum; i++)
            {
                args[i] = lbse.deserialize(buf);
            }
            Object resultObject = invoke(proxyName, methodName, args);
            lbse.serialize(resultObject, buf.clear().addWriteIndex(4));
            return buf;
        }
        catch (Exception e)
        {
            logger.error("远程调用出现失败,代理名称是{}，方法名称是{}，方法参数个数是{}", proxyName, methodName, argsNum, e);
            Licp lbse = threadLocalLbse.get();
            lbse.serialize(e, buf.clear().addWriteIndex(4));
            return buf;
        }
    }
    
    @Override
    public Object catchException(Object data, InternalTask result)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
