# Jfire-simplerpc开发文档
[TOC]
##Jfire-simplerpc是什么
Jfire-simplerpc（以下简称simplerpc）是一款非常简单但却性能强大的rpc框架。旨在提供一个非常简单的rpc调用方式。框架由Jfire-socket提供io能力，由Jfire-fose提供序列化协议。这两者都是各自领域内的佼佼者。特别是Jfire-fose，性能比起Kryo要强上大约46%。强大的性能和简单的使用方式可以为分布式的开发提供更简单的解决方案。并且，借由Jfire-socket提供的加密方案，可以实现在公网上的安全加密调用
##快速入门
首先是需要对外提供服务的rpc的实现类
```java
//接口
public interface Print
{
    public void methodWithoutReturn(String param);
    
    public void par(String[][] ps);
    
    public String methodWithReturn(String param);
    
    public Object[] returnComplexOPbject(ComplexOPbject complexOPbject);
}
//接口实现
public class PrintImpl implements Print
{
    private Logger logger = ConsoleLogFactory.getLogger();
    
    @Override
    public void methodWithoutReturn(String param)
    {
        logger.info("方法被调用");
        logger.info("收到的参数是{}", param);
    }
    
    @Override
    public String methodWithReturn(String param)
    {
        logger.info("收到的参数是{}", param);
        param = param + "追加的末尾信息";
        logger.info("返回的结果是{}", param);
        return param;
    }
    
    @Override
    public Object[] returnComplexOPbject(ComplexOPbject complexOPbject)
    {
        Object[] result = new Object[] { 10, new ComplexOPbject[] { new ComplexOPbject(), new ComplexOPbject() } };
        return result;
    }
    
    @Override
    public void par(String[][] ps)
    {
        // TODO Auto-generated method stub
        
    }
    
}
```
然后是开启rpc服务的代码，可以看到，只有6行，非常简单
```java
//rpc服务端的配置类
RcConfig rcConfig = new RcConfig();
//设置监听端口
rcConfig.setPort(1688);
//设置代理名称，这个功能有点类似命名空间，通过命名空间来区分同名方法
rcConfig.setProxyNames("print");
//设置被开放为rpc调用的实例对象
rcConfig.setImpls(new PrintImpl());
rcServer = new RcServer(rcConfig);
//按照配置启动服务器
rcServer.start();
```
通过上面的代码就开启了一个rpc服务，简单方便。然后是客户端针对这个服务的调用。如下
```java
//设定命名空间，这个需要和服务端一致，设定接口类，服务端ip和端口就可以从工厂类得到接口的rpc客户端实现。该实现是线程安全的，所以这个实例被构造出来后应该保存起来后续使用，而非多次构造。
  Print print = RpcFactory.getProxy("print", Print.class, "127.0.0.1", 1688);
```
##Rpc配置
启动一个rpc服务首先要确定它的配置，包括端口信息等。simplerpc的io实现是基于Jfire-socket的。所以两者的配置具备继承性。关于Jfire-socket可以参考[Jfire-socket][1]。下面我们来看下专属于Rpc的配置信息

+ 命名空间/代理名称:setProxyNames(String...),设置每一个rpc服务的命名空间。每一个命名空间按顺序与设置的服务实例对象一一对应。
+ 开放服务对象:setImpls(Object...),设置每一个rpc服务的实例对象。与命名空间一一对应。

**Rpc的服务开放类并不需要实现特定的接口**，只要求客户端的接口的方法签名和开放类的方法签名一致即可。接口和服务端的开放类则没有任何关联关系。也因为这方面的要求放松了，所以还有一个限制就是**同一个命名空间内，方法不可以重名**
配置完成之后就可以启动rpc服务了
##客户端开发
使用simplerpc的客户端开发非常简单，一行代码就可以搞定。如下
```java
//指定命名空间，接口类，服务器ip和端口。通过工厂类就可以得到客户端的实现了
Print print = RpcFactory.getProxy("print", Print.class, "127.0.0.1", 1688);
```

[1]: http://git.oschina.net/eric_ds/jfire-socket