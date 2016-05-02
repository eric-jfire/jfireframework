package com.jfireframework.codejson;

import java.lang.reflect.Type;
import java.util.Deque;
import java.util.LinkedList;
import org.omg.CORBA.PRIVATE_MEMBER;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.codejson.function.ReaderContext;
import com.jfireframework.codejson.function.WriterContext;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class JsonTool
{
    private static long                       valueOff       = ReflectUtil.getFieldOffset("value", String.class);
    private static Unsafe                     unsafe         = ReflectUtil.getUnsafe();
    private static ThreadLocal<StringCache>   cacheLocal     = new ThreadLocal<StringCache>() {
                                                                 protected StringCache initialValue()
                                                                 {
                                                                     return new StringCache(2048);
                                                                 }
                                                             };
    private static ThreadLocal<Deque<String>> keyStackLocal  = new ThreadLocal<Deque<String>>() {
                                                                 protected Deque<String> initialValue()
                                                                 {
                                                                     return new LinkedList<>();
                                                                 }
                                                             };
    private static ThreadLocal<Deque<Json>>   jsonStackLocal = new ThreadLocal<Deque<Json>>() {
                                                                 protected Deque<Json> initialValue()
                                                                 {
                                                                     return new LinkedList<>();
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
    
    /**
     * 将字符串转换成jsonobject或者是jsonarray
     * 
     * @param str
     * @return
     */
    public static Json fromString(String str)
    {
        Deque<Json> jsonStack = jsonStackLocal.get();
        jsonStack.clear();
        Deque<String> keyStack = keyStackLocal.get();
        keyStack.clear();
        JsonObject lastJsonObject = null;
        JsonArray lastJsonArray = null;
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
        // 当前容器最上层是否是jsonobject，false代表是jsonarray
        boolean isJsonObject = false;
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
                    lastJsonObject = new JsonObject();
                    jsonStack.push(lastJsonObject);
                    isJsonObject = true;
                    if (jsonKey != null)
                    {
                        keyStack.push(jsonKey);
                        jsonKey = null;
                        // 进入新的json，将计数器设置为0
                        flag = 0;
                    }
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
                    if (jsonStack.size() > 1)
                    {
                        Json json = jsonStack.pop();
                        Object ahead = jsonStack.peek();
                        if (ahead instanceof JsonObject)
                        {
                            lastJsonObject = (JsonObject) ahead;
                            lastJsonObject.put(keyStack.pop(), json);
                            isJsonObject = true;
                        }
                        else
                        {
                            lastJsonArray = (JsonArray) ahead;
                            lastJsonArray.add(json);
                            isJsonObject = false;
                        }
                    }
                    else
                    {
                        return (Json) jsonStack.pop();
                    }
                    break;
                case '[':
                    lastJsonArray = new JsonArray();
                    jsonStack.push(lastJsonArray);
                    isJsonObject = false;
                    if (jsonKey != null)
                    {
                        keyStack.push(jsonKey);
                        jsonKey = null;
                        flag = index + 1;
                        break;
                    }
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
                    if (jsonStack.size() > 1)
                    {
                        JsonArray jsonArray = (JsonArray) jsonStack.pop();
                        Object ahead = jsonStack.peek();
                        if (ahead instanceof JsonObject)
                        {
                            lastJsonObject = (JsonObject) ahead;
                            lastJsonObject.put(keyStack.pop(), jsonArray);
                            isJsonObject = true;
                        }
                        else
                        {
                            lastJsonArray = (JsonArray) ahead;
                            lastJsonArray.add(jsonArray);
                            isJsonObject = false;
                        }
                        flag = 0;
                    }
                    else
                    {
                        return (Json) jsonStack.pop();
                    }
                    break;
                case '"':
                    index++;
                    int end = str.indexOf('"', index);
                    Verify.True(end != -1, "json字符串存在异常");
                    if (isJsonObject)
                    {
                        if (jsonKey == null)
                        {
                            // jsonKey = str.substring(index, end);
                            jsonKey = new String(array, index, end - index);
                        }
                        else
                        {
                            lastJsonObject.put(jsonKey, new String(array, index, end - index));
                            jsonKey = null;
                        }
                    }
                    else
                    {
                        lastJsonArray.add(new String(array, index, end - index));
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
                        if (isJsonObject)
                        {
                            if (value != null)
                            {
                                lastJsonObject.put(jsonKey, value);
                            }
                            jsonKey = null;
                            flag = 0;
                        }
                        else
                        {
                            if (value != null)
                            {
                                lastJsonArray.add(value);
                            }
                            flag = index + 1;
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
