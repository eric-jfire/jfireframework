##Jfire-jnet是什么
Jfire-jnet（以下简称jnet）是一个简单却强大的socket框架。底层基于AIO提供强大的网络能力。可以实现在单机上数个线程（个位数）管理十万级别以上的长连接。框架屏蔽了socket的复杂性，为开发者提供了异常简单的编程模型。但是在简单的背后，却蕴藏着强大的控制能力。Jfire-jnet的优点有以下几个

+ 编程模型简单:采用**数据流驱动**的编程模式，开发者实现一个接口就可以开发业务。数据单向流动，同时给予开发者选择的下一个处理单元的权利。使得开发者可以任意控制处理流程。数据流的驱动方式，使得不在需要拦截器等一系列概念，只需要一个简单的数据处理接口就可以实现所有的任务。
+ 强大异步支持线程模型:内建了采用Disruptor思想的异步处理模型。开发者在数据流的处理流程上，可以在一个数据流中使用**同步和异步**两种方式处理数据。基于Disruptor思想的异步处理模型性能十分强大。10线程并发1w的18字节消息，服务器可以在500毫秒左右全部完成echo方式回吐

##Jfire-jnet快速入门
下面展示一个简单的例子让大家对jnet有一个直观的印象。
```java

class myInitListener implements ChannelInitListener
{
    
    // 当通道被建立的时候触发
    @Override
    public void channelInit(ServerChannelInfo serverChannelInfo)
    {
        // 可以设置通道的读取超时时长。默认为3000毫秒
        serverChannelInfo.setReadTimeout(3000);
        // 可以设置通道无数据的读取等待时长。默认为30分钟
        serverChannelInfo.setWaitTimeout(1000 * 60 * 30);
        // 设置数据流的处理器，这步是最重要的，也是业务逻辑的所在。每一个通道的处理器都是通过这种方式新建而来。所以其中的数据都是针对该通道的
        serverChannelInfo.setHandlers(new DataHandler() {
            
            // 这样的数据都是只针对该通道的。因此这种结构很方便用来做登录拦截之类的
            private String loginName;
            
            // data是上一个处理器传递过来的数据，返回值是要给下一个处理器的数据。如果是最开头的处理器，则data就是包解码器解码出来的一个完整报文
            @Override
            public Object handle(Object data, InternalResult result) throws SocketException
            {
                ByteBuf<?> buf = (ByteBuf<?>) data;
                System.out.println("收到消息:"+buf.readString(Charset.forName("utf8")));
                buf.release();
                return "客户端你好，我收到消息了";
            }
        }, new DataHandler() {
            
            @Override
            public Object handle(Object data, InternalResult result) throws SocketException
            {
                // 这里的data就是上一个处理器返回的数据了。处理器之间的顺序就是在代码中他们初始化的顺序
                String value = (String) data;
                // 从内存缓存池中获取一个内存缓存区用来写出数据
                ByteBuf<?> buf = DirectByteBufPool.getInstance().get(100);
                buf.writeString(value, Charset.forName("utf8"));
                buf.writeByte((byte) '\r');
                buf.writeByte((byte) '\n');
                // 末尾的处理器一定要返回ByteBuf类型的数据。这样框架就会自动将这个数据通过socket发送出去
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
        // 服务端监听的端口
        config.setPort(80);
        config.setInitListeners(new myInitListener());
        // 设置包解码器。包解码器用来从tcp的数据流中截取出一个完整的tcp报文
        // 这个解码器是行解码器。使用换行符进行报文切割
        // 当然，开发者也可以根据自己的业务需求，自行定制包解码器。框架本身提供了4种最为常见的包解码器。
        config.setFrameDecodec(new LineBasedFrameDecodec(1000));
        // 使用上面的配置新建一个服务端对象
        AioServer aioServer = new AioServer(config);
        // 启动服务端
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
        // 使用对应的参数链接服务端
        aioClient.connect();
		//返回一个future实现。在收到消息后
        Future<?> future = aioClient.write("你好，这里是客户端");
        System.out.println(future.get());
        // 服务端关闭
        aioServer.stop();
    }
}


```
通过阅读上面的例子，相信开发者对于如何编写基于Jnet的socket程序已经有了初步的感官认识。通过上面的例子，也可以看到，基于Jnet开发，是非常简单和方便的。
对于服务端，开发者只需要关注
+ 包解码器的实现。框架内置提供了4中最为常见的包解码器
+ 服务端收到的数据流中，对数据流处理实现的业务处理器。

对于客户端，开发者只需要关注
+ 包解码器的实现
+ 需要发送的数据，往外流动的数据的业务处理过程
+ 收到的数据，往内流动的业务处理过程

