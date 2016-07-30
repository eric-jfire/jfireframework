package com.jfireframework.socket.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.Port;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.jnet.client.AioClient;
import com.jfireframework.jnet.common.channel.ChannelInitListener;
import com.jfireframework.jnet.common.channel.JnetChannel;
import com.jfireframework.jnet.common.decodec.TotalLengthFieldBasedFrameDecoder;
import com.jfireframework.jnet.common.decodec.TotalLengthFieldBasedFrameDecoderByHeap;
import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.handler.LengthPreHandler;
import com.jfireframework.jnet.common.result.InternalTask;
import com.jfireframework.jnet.server.AioServer;
import com.jfireframework.jnet.server.util.DisruptorWaitMode;
import com.jfireframework.jnet.server.util.ServerConfig;
import com.jfireframework.jnet.server.util.WorkMode;

public class EchoTest
{
    private int    threadCountStart = 1;
    private int    threadCountEnd   = 80;
    private int    sendCount        = 100;
    private String ip               = "127.0.0.1";
    private int    port             = 5689;
    
    @Test
    public void test() throws Throwable
    {
        ServerConfig config = new ServerConfig();
        config.setSocketThreadSize(4);
        config.setChannelCapacity(16);
        config.setInitListener(new ChannelInitListener() {
            
            @Override
            public void channelInit(JnetChannel serverChannelInfo)
            {
                serverChannelInfo.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, 500));
                serverChannelInfo.setHandlers(new EchoHandler());
            }
        });
        config.setPort(port);
        AioServer aioServer = new AioServer(config);
        aioServer.start();
        List<Long> timeCount = new LinkedList<>();
        for (int index = threadCountStart; index <= threadCountEnd; index++)
        {
            Thread[] threads = new Thread[index];
            for (int i = 0; i < threads.length; i++)
            {
                threads[i] = new Thread(new Runnable() {
                    
                    @Override
                    public void run()
                    {
                        try
                        {
                            connecttest();
                        }
                        catch (Throwable e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, "测试线程_" + index + "_" + i);
                threads[i].start();
            }
            Timewatch timewatch = new Timewatch();
            timewatch.start();
            for (int i = 0; i < threads.length; i++)
            {
                threads[i].join();
            }
            timewatch.end();
            System.out.println("线程数量：" + index + ",运行完毕:" + timewatch.getTotal());
            timeCount.add(timewatch.getTotal());
        }
        exportExcel(timeCount);
    }
    
    private void exportExcel(List<Long> timeCount)
    {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row threadRow = sheet.createRow(0);
        Cell cell = threadRow.createCell(0);
        cell.setCellValue("线程数");
        Row timeRow = sheet.createRow(1);
        cell = timeRow.createCell(0);
        cell.setCellValue("时间");
        int i = 1;
        for (Long each : timeCount)
        {
            cell = threadRow.createCell(i);
            // 线程数
            cell.setCellValue(String.valueOf(i));
            cell = timeRow.createCell(i);
            cell.setCellValue(each.toString());
            i += 1;
        }
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
            FileOutputStream outputStream = new FileOutputStream("target" + File.separator + format.format(new Date()) + ".xls");
            workbook.write(outputStream);
            outputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
    }
    
    public void connecttest() throws Throwable
    {
        AioClient client = new AioClient(false);
        client.setAddress(ip);
        client.setPort(port);
        client.setWriteHandlers(new DataHandler() {
            
            @Override
            public Object handle(Object data, InternalTask result) throws JnetException
            {
                ByteBuf<?> buf = DirectByteBuf.allocate(100);
                buf.addWriteIndex(4);
                buf.writeString((String) data);
                return buf;
            }
            
            @Override
            public Object catchException(Object data, InternalTask result)
            {
                // ((Throwable) data).printStackTrace();
                return data;
            }
        }, new LengthPreHandler(0, 4));
        client.setInitListener(new ChannelInitListener() {
            
            @Override
            public void channelInit(JnetChannel jnetChannel)
            {
                jnetChannel.setFrameDecodec(new TotalLengthFieldBasedFrameDecoderByHeap(0, 4, 4, 500));
                jnetChannel.setCapacity(2048);
                jnetChannel.setHandlers(new DataHandler() {
                    
                    @Override
                    public Object handle(Object data, InternalTask result) throws JnetException
                    {
                        // System.out.println("收到数据");
                        ByteBuf<?> buf = (ByteBuf<?>) data;
                        String value = null;
                        value = buf.readString();
                        buf.release();
                        return value;
                    }
                    
                    @Override
                    public Object catchException(Object data, InternalTask result)
                    {
                        // System.err.println("客户端");
                        // ((Throwable) data).printStackTrace();
                        return data;
                    }
                });
            }
        });
        for (int i = 0; i < sendCount; i++)
        {
            try
            {
                client.connect().write("123456");
            }
            catch (Exception e)
            {
            }
        }
        Future<?> future = client.connect().write("987654321");
        try
        {
            Assert.assertEquals("987654321", (String) future.get(20000, TimeUnit.MILLISECONDS));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        client.close();
    }
}
