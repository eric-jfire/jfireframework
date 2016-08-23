package com.jfireframework.socket.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.jnet2.client.AioClient;
import com.jfireframework.jnet2.common.channel.ChannelInitListener;
import com.jfireframework.jnet2.common.channel.JnetChannel;
import com.jfireframework.jnet2.common.decodec.TotalLengthFieldBasedFrameDecoder;
import com.jfireframework.jnet2.common.exception.JnetException;
import com.jfireframework.jnet2.common.handler.DataHandler;
import com.jfireframework.jnet2.common.handler.LengthPreHandler;
import com.jfireframework.jnet2.common.result.InternalResult;
import com.jfireframework.jnet2.server.AioServer;
import com.jfireframework.jnet2.server.util.AcceptMode;
import com.jfireframework.jnet2.server.util.ExecutorMode;
import com.jfireframework.jnet2.server.util.PushMode;
import com.jfireframework.jnet2.server.util.ServerConfig;
import com.jfireframework.jnet2.server.util.WorkMode;

public class SpeedTest
{
    private int    threadCountStart = 1;
    private int    threadCountEnd   = 200;
    private int    sendCount        = 10000;
    private String ip               = "192.168.10.51";
    private int    port             = 7789;
    
    @Test
    public void test() throws Throwable
    {
        ServerConfig config = new ServerConfig();
        config.setAcceptMode(AcceptMode.CAPACITY);
        config.setPushMode(PushMode.OFF);
        config.setWorkMode(WorkMode.SYNC);
        config.setSocketThreadNum(40);
        config.setAsyncCapacity(16);
        config.setChannelCapacity(64);
        config.setExecutorMode(ExecutorMode.FIX);
        config.setAsyncThreadNum(16);
        config.setInitListener(
                new ChannelInitListener() {
                    
                    @Override
                    public void channelInit(JnetChannel serverChannelInfo)
                    {
                        serverChannelInfo.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, 500));
                        serverChannelInfo.setHandlers(new EchoHandler());
                    }
                }
        );
        config.setPort(port);
        AioServer aioServer = new AioServer(config);
        aioServer.start();
        List<Long> timeCount = new LinkedList<>();
        final CpuCachePadingInt result = new CpuCachePadingInt(0);
        ExecutorService pool = Executors.newCachedThreadPool();
        for (int index = threadCountStart; index <= threadCountEnd; index++)
        {
            final CyclicBarrier barrier = new CyclicBarrier(index);
            final CountDownLatch latch = new CountDownLatch(index);
            for (int i = 0; i < index; i++)
            {
               pool.submit( new Runnable() {
                            
                            @Override
                            public void run()
                            {
                                try
                                {
                                    barrier.await();
                                    connecttest(result);
                                    latch.countDown();
                                }
                                catch (Throwable e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
            Timewatch timewatch = new Timewatch();
            timewatch.start();
          latch.await();
            timewatch.end();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            if (result.value() == 0)
            {
                System.out.println(format.format(new Date()) + ",线程数量：" + index + ",运行完毕:" + timewatch.getTotal());
            }
            else if (result.value() == 1)
            {
                System.err.println(format.format(new Date()) + ",线程数量：" + index + ",运行过程中失败");
            }
            else
            {
                System.err.println(format.format(new Date()) + ",线程数量：" + index + ",结束异常:");
            }
            timeCount.add(timewatch.getTotal());
            result.set(0);
        }
        exportExcel(timeCount, config);
    }
    
    private void exportExcel(List<Long> timeCount, ServerConfig serverConfig)
    {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row threadRow = sheet.createRow(0);
        Cell cell = threadRow.createCell(0);
        cell.setCellValue("线程数");
        Row timeRow = sheet.createRow(1);
        cell = timeRow.createCell(0);
        cell.setCellValue("时间");
        int i = 0;
        for (Long each : timeCount)
        {
            cell = threadRow.createCell(i + 1);
            // 线程数
            cell.setCellValue(String.valueOf(i + threadCountStart));
            cell = timeRow.createCell(i + 1);
            cell.setCellValue(each.toString());
            i += 1;
        }
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
            FileOutputStream outputStream = new FileOutputStream("target" + File.separator + serverConfig.getAcceptMode().name() + "_" + format.format(new Date()) + ".xls");
            workbook.write(outputStream);
            outputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
    }
    
    public void connecttest(CpuCachePadingInt result) throws Throwable
    {
        AioClient client = new AioClient(true);
        client.setAddress(ip);
        client.setPort(port);
        client.setWriteHandlers(
                new DataHandler() {
                    
                    @Override
                    public Object handle(Object data, InternalResult result) throws JnetException
                    {
                        ByteBuf<?> buf = DirectByteBuf.allocate(100);
                        buf.addWriteIndex(4);
                        buf.writeString((String) data);
                        return buf;
                    }
                    
                    @Override
                    public Object catchException(Object data, InternalResult result)
                    {
                        // ((Throwable) data).printStackTrace();
                        return data;
                    }
                }, new LengthPreHandler(0, 4)
        );
        final CountDownLatch countDownLatch = new CountDownLatch(sendCount);
        client.setInitListener(
                new ChannelInitListener() {
                    
                    @Override
                    public void channelInit(JnetChannel jnetChannel)
                    {
                        jnetChannel.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, 500));
                        jnetChannel.setHandlers(
                                new DataHandler() {
                                    
                                    @Override
                                    public Object handle(Object data, InternalResult result) throws JnetException
                                    {
//                                         System.out.println("收到数据");
                                        ByteBuf<?> buf = (ByteBuf<?>) data;
                                        String value = null;
                                        value = buf.readString();
                                        buf.release();
                                        countDownLatch.countDown();
                                        return value;
                                    }
                                    
                                    @Override
                                    public Object catchException(Object data, InternalResult result)
                                    {
                                        // System.err.println("客户端");
                                        // ((Throwable) data).printStackTrace();
                                        return data;
                                    }
                                }
                        );
                    }
                }
        );
        for (int i = 0; i < sendCount; i++)
        {
            try
            {
                client.connect().write("123456");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                result.compareAndSwap(0, 1);
            }
        }
        // Future<?> future = client.connect().write("987654321");
        try
        {
            countDownLatch.await(30, TimeUnit.SECONDS);
            // Assert.assertEquals("987654321", (String) future.get(50000,
            // TimeUnit.SECONDS));
        }
        catch (Exception e)
        {
            result.compareAndSwap(0, 2);
        }
        client.close();
    }
}
