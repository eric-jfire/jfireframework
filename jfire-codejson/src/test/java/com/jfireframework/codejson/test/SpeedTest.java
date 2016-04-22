package com.jfireframework.codejson.test;

import org.junit.Ignore;
import org.junit.Test;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.test.simple.Home;

public class SpeedTest extends Support
{
    
    @Test
    public void Test()
    {
        WriteStrategy strategy = new WriteStrategy();
        strategy.setUseTracker(true);
        strategy.write(data);
    }
    
    @Test
    // @Ignore
    public void writeSpeedTest() throws JsonProcessingException
    {
        logger.debug("codejson的输出是\n\n{}\n\n", JsonTool.write(data));
        WriteStrategy strategy = new WriteStrategy();
        strategy.setUseTracker(true);
        strategy.write(data);
        ObjectMapper mapper = new ObjectMapper();
        Gson gson = new Gson();
        System.out.println(gson.toJson(data));
        JSON.toJSONString(data);
        mapper.writeValueAsString(data);
        Timewatch timewatch = new Timewatch();
        int count = 1000000;
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            JSON.toJSONString(data);
        }
        timewatch.end();
        logger.info("fastjson输出耗时：{}", timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            JsonTool.write(data);
            // strategy.write(data);
        }
        timewatch.end();
        logger.info("codejson输出耗时：{}", timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            mapper.writeValueAsString(data);
        }
        timewatch.end();
        logger.info("jackson2输出耗时：{}", timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            gson.toJson(data);
        }
        timewatch.end();
        logger.info("gson输出耗时：{}", timewatch.getTotal());
    }
    
    @Test
    @Ignore
    public void writeSpeedTest2()
    {
        Home home = new Home();
        Timewatch timewatch = new Timewatch();
        int count = 1000000;
        JsonTool.write(home);
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            JSON.toJSONString(home);
        }
        timewatch.end();
        logger.info("简单测试fastjson输出耗时：{}", timewatch.getTotal());
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            JsonTool.write(home);
        }
        timewatch.end();
        logger.info("简单测试codejson输出耗时：{}", timewatch.getTotal());
        
    }
    
}
