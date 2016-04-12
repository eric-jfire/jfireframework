package com.jfireframework.codejson.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import org.junit.Test;
import com.jfireframework.codejson.JsonObject;
import com.jfireframework.codejson.JsonTool;

public class ReadComment
{
    @Test
    public void test() throws IOException, URISyntaxException
    {
        File configFile = new File(this.getClass().getClassLoader().getResource("config.json1").toURI());
        FileInputStream inputStream = new FileInputStream(configFile);
        byte[] array = new byte[inputStream.available()];
        inputStream.read(array);
        JsonObject jsonObject = (JsonObject) JsonTool.fromString(new String(array));
        System.out.println(jsonObject.getWString("imageActionName"));
    }
}
