##Jfire-jnet��ʲô
Jfire-jnet�����¼��jnet����һ����ȴǿ���socket��ܡ��ײ����AIO�ṩǿ�����������������ʵ���ڵ����������̣߳���λ��������ʮ�򼶱����ϵĳ����ӡ����������socket�ĸ����ԣ�Ϊ�������ṩ���쳣�򵥵ı��ģ�͡������ڼ򵥵ı���ȴ�̲���ǿ��Ŀ���������Jfire-jnet���ŵ������¼���

+ ���ģ�ͼ�:����**����������**�ı��ģʽ��������ʵ��һ���ӿھͿ��Կ���ҵ�����ݵ���������ͬʱ���迪����ѡ�����һ������Ԫ��Ȩ����ʹ�ÿ����߿���������ƴ������̡���������������ʽ��ʹ�ò�����Ҫ��������һϵ�и��ֻ��Ҫһ���򵥵����ݴ���ӿھͿ���ʵ�����е�����
+ ǿ���첽֧���߳�ģ��:�ڽ��˲���Disruptor˼����첽����ģ�͡����������������Ĵ��������ϣ�������һ����������ʹ��**ͬ�����첽**���ַ�ʽ�������ݡ�����Disruptor˼����첽����ģ������ʮ��ǿ��10�̲߳���1w��18�ֽ���Ϣ��������������500��������ȫ�����echo��ʽ����

##Jfire-jnet��������
����չʾһ���򵥵������ô�Ҷ�jnet��һ��ֱ�۵�ӡ��
```java

class myInitListener implements ChannelInitListener
{
    
    // ��ͨ����������ʱ�򴥷�
    @Override
    public void channelInit(ServerChannelInfo serverChannelInfo)
    {
        // ��������ͨ���Ķ�ȡ��ʱʱ����Ĭ��Ϊ3000����
        serverChannelInfo.setReadTimeout(3000);
        // ��������ͨ�������ݵĶ�ȡ�ȴ�ʱ����Ĭ��Ϊ30����
        serverChannelInfo.setWaitTimeout(1000 * 60 * 30);
        // �����������Ĵ��������ⲽ������Ҫ�ģ�Ҳ��ҵ���߼������ڡ�ÿһ��ͨ���Ĵ���������ͨ�����ַ�ʽ�½��������������е����ݶ�����Ը�ͨ����
        serverChannelInfo.setHandlers(new DataHandler() {
            
            // ���������ݶ���ֻ��Ը�ͨ���ġ�������ֽṹ�ܷ�����������¼����֮���
            private String loginName;
            
            // data����һ�����������ݹ��������ݣ�����ֵ��Ҫ����һ�������������ݡ�������ͷ�Ĵ���������data���ǰ����������������һ����������
            @Override
            public Object handle(Object data, InternalResult result) throws SocketException
            {
                ByteBuf<?> buf = (ByteBuf<?>) data;
                System.out.println("�յ���Ϣ:"+buf.readString(Charset.forName("utf8")));
                buf.release();
                return "�ͻ�����ã����յ���Ϣ��";
            }
        }, new DataHandler() {
            
            @Override
            public Object handle(Object data, InternalResult result) throws SocketException
            {
                // �����data������һ�����������ص������ˡ�������֮���˳������ڴ��������ǳ�ʼ����˳��
                String value = (String) data;
                // ���ڴ滺����л�ȡһ���ڴ滺��������д������
                ByteBuf<?> buf = DirectByteBufPool.getInstance().get(100);
                buf.writeString(value, Charset.forName("utf8"));
                buf.writeByte((byte) '\r');
                buf.writeByte((byte) '\n');
                // ĩβ�Ĵ�����һ��Ҫ����ByteBuf���͵����ݡ�������ܾͻ��Զ����������ͨ��socket���ͳ�ȥ
                return buf;
            }
        });
    }
    
}

public class BaseServerTest
{
    private Logger logger = ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
    
    @Test
    public void test() throws IOException, InterruptedException, ExecutionException, SocketException
    {
        ServerConfig config = new ServerConfig();
        // ����˼����Ķ˿�
        config.setPort(80);
        config.setInitListeners(new myInitListener());
        // ���ð�����������������������tcp���������н�ȡ��һ��������tcp����
        // ������������н�������ʹ�û��з����б����и�
        // ��Ȼ��������Ҳ���Ը����Լ���ҵ���������ж��ư�����������ܱ����ṩ��4����Ϊ�����İ���������
        config.setFrameDecodec(new LineBasedFrameDecodec(1000));
        // ʹ������������½�һ������˶���
        AioServer aioServer = new AioServer(config);
        // ���������
        aioServer.start();
        AioClient aioClient = new FutureClient();
        aioClient.setAddress("127.0.0.1").setPort(80);
        aioClient.setFrameDecodec(new LineBasedFrameDecodec(100));
        aioClient.setWriteHandlers(new DataHandler() {
            
            @Override
            public Object handle(Object data, InternalResult result) throws SocketException
            {
                String value = (String) data;
                ByteBuf<?> buf = DirectByteBufPool.getInstance().get(100);
                buf.writeString(value, Charset.forName("utf8"));
                buf.writeByte((byte) '\r');
                buf.writeByte((byte) '\n');
                return buf;
            }
        });
        aioClient.setReadHandlers(new DataHandler() {
            
            @Override
            public Object handle(Object data, InternalResult result) throws SocketException
            {
                ByteBuf<?> buf = (ByteBuf<?>) data;
                String value = buf.readString(Charset.forName("utf8"));
                buf.release();
                return value;
            }
        });
        // ʹ�ö�Ӧ�Ĳ������ӷ����
        aioClient.connect();
		//����һ��futureʵ�֡����յ���Ϣ��
        Future<?> future = aioClient.write("��ã������ǿͻ���");
        System.out.println(future.get());
        // ����˹ر�
        aioServer.stop();
    }
}


```
ͨ���Ķ���������ӣ����ſ����߶�����α�д����Jnet��socket�����Ѿ����˳����ĸй���ʶ��ͨ����������ӣ�Ҳ���Կ���������Jnet�������Ƿǳ��򵥺ͷ���ġ�
���ڷ���ˣ�������ֻ��Ҫ��ע
+ ����������ʵ�֡���������ṩ��4����Ϊ�����İ�������
+ ������յ����������У�������������ʵ�ֵ�ҵ��������

���ڿͻ��ˣ�������ֻ��Ҫ��ע
+ ����������ʵ��
+ ��Ҫ���͵����ݣ��������������ݵ�ҵ�������
+ �յ������ݣ�����������ҵ�������

