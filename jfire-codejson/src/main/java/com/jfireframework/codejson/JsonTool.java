package com.jfireframework.codejson;

import java.lang.reflect.Type;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.codejson.function.ReaderContext;
import com.jfireframework.codejson.function.WriterContext;

public class JsonTool
{
    
    public static void initClassPool(ClassLoader classLoader)
    {
        ReaderContext.initClassPool(classLoader);
        WriterContext.initClassPool(classLoader);
    }
    
    private static ThreadLocal<StringCache> cacheLocal = new ThreadLocal<StringCache>() {
        protected StringCache initialValue()
        {
            return new StringCache(2048);
        }
    };
    
    public static String write(Object value)
    {
        StringCache cache = cacheLocal.get();
        cache.clear();
        WriterContext.write(value, cache);
        return cache.toString();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T read(Type entityClass, String str)
    {
        return (T) ReaderContext.getReader(entityClass).read(entityClass, fromString(str));
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T read(Type entityClass, Json json)
    {
        return (T) ReaderContext.getReader(entityClass).read(entityClass, json);
    }
    
    private static final int NONE       = 0;
    private static final int JSONOBJECT = 1;
    private static final int JSONARRAY  = 2;
    
    /**
     * 将字符串转换成jsonobject或者是jsonarray
     * 
     * @param str
     * @return
     */
    public static Json fromString(String str)
    {
        JsonObject lastJsonObject = null;
        JsonArray lastJsonArray = null;
        int nodeState = NONE;
        // 可读信息标志位
        int flag = 0;
        // 当前读取位置
        int index = 0;
        int length = str.length();
        // char[] array = (char[]) unsafe.getObject(str, valueOff);
        char[] array = new char[str.length()];
        str.getChars(0, length, array, 0);
        // 是否开始读取字符串
        boolean strStartRead = false;
        String jsonKey = null;
        comment: while (index < length)
        {
            char c = array[index];
            switch (c)
            {
                case '/':
                    if (array[index + 1] == '*')
                    {
                        int end = str.indexOf("*/", index);
                        if (end == -1)
                        {
                            throw new RuntimeException("json字符串存在问题");
                        }
                        else
                        {
                            index = end + 2;
                            flag = flag == 0 ? 0 : index;
                            continue comment;
                        }
                    }
                    else
                    {
                        break;
                    }
                case '{':
                    JsonObject tmp = new JsonObject();
                    switch (nodeState)
                    {
                        case NONE:
                        {
                            break;
                        }
                        case JSONOBJECT:
                        {
                            lastJsonObject.put(jsonKey, tmp);
                            tmp.setParentNode(lastJsonObject);
                            jsonKey = null;
                            flag = 0;
                            break;
                        }
                        case JSONARRAY:
                        {
                            lastJsonArray.add(tmp);
                            tmp.setParentNode(lastJsonArray);
                            break;
                        }
                    }
                    nodeState = JSONOBJECT;
                    lastJsonObject = tmp;
                    break;
                case '}':
                    // 如果计数器不为0，那就意味着还有非字符串形式的值尚未读取
                    if (flag != 0)
                    {
                        Object value = getNotStrValue(flag, index - 1, array);
                        if (value != null)
                        {
                            lastJsonObject.put(jsonKey, value);
                        }
                        jsonKey = null;
                        flag = 0;
                    }
                    if (lastJsonObject.hasParentNode())
                    {
                        // Json json = jsonStack.pop();
                        Json parentNode = lastJsonObject.getParentNode();
                        if (parentNode instanceof JsonObject)
                        {
                            lastJsonObject = (JsonObject) parentNode;
                            nodeState = JSONOBJECT;
                        }
                        else
                        {
                            lastJsonArray = (JsonArray) parentNode;
                            nodeState = JSONARRAY;
                        }
                    }
                    else
                    {
                        return (Json) lastJsonObject;
                    }
                    break;
                case '[':
                    JsonArray tmp1 = new JsonArray();
                    switch (nodeState)
                    {
                        case NONE:
                        {
                            
                            break;
                        }
                        case JSONOBJECT:
                        {
                            lastJsonObject.put(jsonKey, tmp1);
                            tmp1.setParentNode(lastJsonObject);
                            jsonKey = null;
                            break;
                        }
                        case JSONARRAY:
                        {
                            lastJsonArray.add(tmp1);
                            tmp1.setParentNode(lastJsonArray);
                            break;
                        }
                    }
                    nodeState = JSONARRAY;
                    lastJsonArray = tmp1;
                    flag = index + 1;
                    break;
                case ']':
                    if (flag != 0 && flag != index)
                    {
                        Object value = getNotStrValue(flag, index - 1, array);
                        if (value != null)
                        {
                            lastJsonArray.add(value);
                        }
                        flag = 0;
                    }
                    if (lastJsonArray.hasParentNode())
                    {
                        Json parentNode1 = lastJsonArray.getParentNode();
                        if (parentNode1 instanceof JsonObject)
                        {
                            lastJsonObject = (JsonObject) parentNode1;
                            // lastJsonObject.put(keyStack.pop(),
                            // lastJsonArray);
                            nodeState = JSONOBJECT;
                        }
                        else
                        {
                            // ((JsonArray) parentNode).add(lastJsonArray);
                            lastJsonArray = (JsonArray) parentNode1;
                            nodeState = JSONARRAY;
                        }
                        flag = 0;
                    }
                    else
                    {
                        return (Json) lastJsonArray;
                    }
                    break;
                case '"':
                    index++;
                    int end = str.indexOf('"', index);
                    Verify.True(end != -1, "json字符串存在异常");
                    if (nodeState == JSONOBJECT)
                    {
                        if (jsonKey == null)
                        {
                            jsonKey = new String(array, index, end - index);
                        }
                        else
                        {
                            lastJsonObject.put(jsonKey, new String(array, index, end - index));
                            jsonKey = null;
                        }
                    }
                    else if (nodeState == JSONARRAY)
                    {
                        lastJsonArray.add(new String(array, index, end - index));
                    }
                    else
                    {
                        throw new RuntimeException("错误的json格式");
                    }
                    flag = 0;
                    strStartRead = false;
                    index = end + 1;
                    continue comment;
                case ':':
                    if (strStartRead)
                    {
                        break;
                    }
                    if (flag == 0)
                    {
                        flag = index + 1;
                    }
                    break;
                case ',':
                    if (strStartRead)
                    {
                        break;
                    }
                    if (flag != 0)
                    {
                        Object value = getNotStrValue(flag, index - 1, array);
                        if (nodeState == JSONOBJECT)
                        {
                            if (value != null)
                            {
                                lastJsonObject.put(jsonKey, value);
                            }
                            jsonKey = null;
                            flag = 0;
                        }
                        else if (nodeState == JSONARRAY)
                        {
                            if (value != null)
                            {
                                lastJsonArray.add(value);
                            }
                            flag = index + 1;
                        }
                        else
                        {
                            throw new RuntimeException("错误的json格式");
                        }
                    }
                    break;
                default:
                    break;
            }
            index++;
        }
        throw new RuntimeException("json字符串存在错误");
    }
    
    private static Object getNotStrValue(int flag, int index, char[] value)
    {
        checkhead: while (true)
        {
            switch (value[flag])
            {
                case ' ':
                    flag++;
                    break;
                case '\t':
                    flag++;
                    break;
                case '\r':
                    flag++;
                    break;
                case '\n':
                    flag++;
                    break;
                default:
                    break checkhead;
            }
        }
        checkend: while (true)
        {
            switch (value[index])
            {
                case ' ':
                    index--;
                    break;
                case '\t':
                    index--;
                    break;
                case '\r':
                    index--;
                    break;
                case '\n':
                    index--;
                    break;
                default:
                    break checkend;
            }
        }
        String tmp = new String(value, flag, index - flag + 1);
        if (tmp.equals("true"))
        {
            return Boolean.TRUE;
        }
        else if (tmp.equals("false"))
        {
            return Boolean.FALSE;
        }
        else if (tmp.equals("null"))
        {
            return null;
        }
        else if (tmp.contains("."))
        {
            return Double.valueOf(tmp);
        }
        else if (tmp.equals("{"))
        {
            return null;
        }
        else
        {
            return Long.valueOf(tmp);
        }
    }
    
    public static String toString(Object value)
    {
        return write(value);
    }
}
