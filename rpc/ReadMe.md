# Jfire-simplerpc�����ĵ�
[TOC]
##Jfire-simplerpc��ʲô
Jfire-simplerpc�����¼��simplerpc����һ��ǳ��򵥵�ȴ����ǿ���rpc��ܡ�ּ���ṩһ���ǳ��򵥵�rpc���÷�ʽ�������Jfire-socket�ṩio��������Jfire-fose�ṩ���л�Э�顣�����߶��Ǹ��������ڵ�ٮٮ�ߡ��ر���Jfire-fose�����ܱ���KryoҪǿ�ϴ�Լ46%��ǿ������ܺͼ򵥵�ʹ�÷�ʽ����Ϊ�ֲ�ʽ�Ŀ����ṩ���򵥵Ľ�����������ң�����Jfire-socket�ṩ�ļ��ܷ���������ʵ���ڹ����ϵİ�ȫ���ܵ���
##��������
��������Ҫ�����ṩ�����rpc��ʵ����
```java
//�ӿ�
public interface Print
{
    public void methodWithoutReturn(String param);
    
    public void par(String[][] ps);
    
    public String methodWithReturn(String param);
    
    public Object[] returnComplexOPbject(ComplexOPbject complexOPbject);
}
//�ӿ�ʵ��
public class PrintImpl implements Print
{
    private Logger logger = ConsoleLogFactory.getLogger();
    
    @Override
    public void methodWithoutReturn(String param)
    {
        logger.info("����������");
        logger.info("�յ��Ĳ�����{}", param);
    }
    
    @Override
    public String methodWithReturn(String param)
    {
        logger.info("�յ��Ĳ�����{}", param);
        param = param + "׷�ӵ�ĩβ��Ϣ";
        logger.info("���صĽ����{}", param);
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
Ȼ���ǿ���rpc����Ĵ��룬���Կ�����ֻ��6�У��ǳ���
```java
//rpc����˵�������
RcConfig rcConfig = new RcConfig();
//���ü����˿�
rcConfig.setPort(1688);
//���ô������ƣ���������е����������ռ䣬ͨ�������ռ�������ͬ������
rcConfig.setProxyNames("print");
//���ñ�����Ϊrpc���õ�ʵ������
rcConfig.setImpls(new PrintImpl());
rcServer = new RcServer(rcConfig);
//������������������
rcServer.start();
```
ͨ������Ĵ���Ϳ�����һ��rpc���񣬼򵥷��㡣Ȼ���ǿͻ�������������ĵ��á�����
```java
//�趨�����ռ䣬�����Ҫ�ͷ����һ�£��趨�ӿ��࣬�����ip�Ͷ˿ھͿ��Դӹ�����õ��ӿڵ�rpc�ͻ���ʵ�֡���ʵ�����̰߳�ȫ�ģ��������ʵ�������������Ӧ�ñ�����������ʹ�ã����Ƕ�ι��졣
  Print print = RpcFactory.getProxy("print", Print.class, "127.0.0.1", 1688);
```
##Rpc����
����һ��rpc��������Ҫȷ���������ã������˿���Ϣ�ȡ�simplerpc��ioʵ���ǻ���Jfire-socket�ġ��������ߵ����þ߱��̳��ԡ�����Jfire-socket���Բο�[Jfire-socket][1]����������������ר����Rpc��������Ϣ

+ �����ռ�/��������:setProxyNames(String...),����ÿһ��rpc����������ռ䡣ÿһ�������ռ䰴˳�������õķ���ʵ������һһ��Ӧ��
+ ���ŷ������:setImpls(Object...),����ÿһ��rpc�����ʵ�������������ռ�һһ��Ӧ��

**Rpc�ķ��񿪷��ಢ����Ҫʵ���ض��Ľӿ�**��ֻҪ��ͻ��˵Ľӿڵķ���ǩ���Ϳ�����ķ���ǩ��һ�¼��ɡ��ӿںͷ���˵Ŀ�������û���κι�����ϵ��Ҳ��Ϊ�ⷽ���Ҫ������ˣ����Ի���һ�����ƾ���**ͬһ�������ռ��ڣ���������������**
�������֮��Ϳ�������rpc������
##�ͻ��˿���
ʹ��simplerpc�Ŀͻ��˿����ǳ��򵥣�һ�д���Ϳ��Ը㶨������
```java
//ָ�������ռ䣬�ӿ��࣬������ip�Ͷ˿ڡ�ͨ��������Ϳ��Եõ��ͻ��˵�ʵ����
Print print = RpcFactory.getProxy("print", Print.class, "127.0.0.1", 1688);
```

[1]: http://git.oschina.net/eric_ds/jfire-socket