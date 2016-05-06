package com.jfireframework.codejson.function;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.impl.write.array.BooleanArrayWriter;
import com.jfireframework.codejson.function.impl.write.array.ByteArrayWriter;
import com.jfireframework.codejson.function.impl.write.array.CharArrayWriter;
import com.jfireframework.codejson.function.impl.write.array.DoubleArrayWriter;
import com.jfireframework.codejson.function.impl.write.array.FloatArrayWriter;
import com.jfireframework.codejson.function.impl.write.array.IntArrayWriter;
import com.jfireframework.codejson.function.impl.write.array.LongArrayWriter;
import com.jfireframework.codejson.function.impl.write.array.ShortArrayWriter;
import com.jfireframework.codejson.function.impl.write.array.StringArrayWriter;
import com.jfireframework.codejson.function.impl.write.extra.ArrayListWriter;
import com.jfireframework.codejson.function.impl.write.extra.DateWriter;
import com.jfireframework.codejson.function.impl.write.wrapper.BooleanWriter;
import com.jfireframework.codejson.function.impl.write.wrapper.ByteWriter;
import com.jfireframework.codejson.function.impl.write.wrapper.CharacterWriter;
import com.jfireframework.codejson.function.impl.write.wrapper.DoubleWriter;
import com.jfireframework.codejson.function.impl.write.wrapper.FloatWriter;
import com.jfireframework.codejson.function.impl.write.wrapper.IntegerWriter;
import com.jfireframework.codejson.function.impl.write.wrapper.LongWriter;
import com.jfireframework.codejson.function.impl.write.wrapper.ShortWriter;
import com.jfireframework.codejson.function.impl.write.wrapper.StringWriter;
import com.jfireframework.codejson.tracker.Tracker;

public class WriteStrategy implements Strategy
{
    private Map<Class<?>, JsonWriter> trackerType   = new HashMap<>();
    private Map<Class<?>, JsonWriter> typeStrategy  = new HashMap<>();
    private Map<String, JsonWriter>   fieldStrategy = new HashMap<>();
    private Set<String>               ignoreFields  = new HashSet<>();
    private Map<String, String>       renameFields  = new HashMap<>();
    private JsonWriter                writer;
    private boolean                   useTracker    = false;
    private ThreadLocal<StringCache>  cacheLocal    = new ThreadLocal<StringCache>() {
                                                        protected StringCache initialValue()
                                                        {
                                                            return new StringCache();
                                                        }
                                                    };
    private ThreadLocal<Tracker>      trackerLocal  = new ThreadLocal<Tracker>() {
                                                        protected Tracker initialValue()
                                                        {
                                                            return new Tracker();
                                                        }
                                                    };
    private boolean                   writeEnumName = true;
    
    public WriteStrategy()
    {
        // 用来保证如果类的属性是object的情况，而实际内容是这些的，可以正确的解析。特别是包装类，避免他们被二次解析生成特别的输出类
        typeStrategy.put(String.class, new StringWriter());
        typeStrategy.put(Double.class, new DoubleWriter());
        typeStrategy.put(Float.class, new FloatWriter());
        typeStrategy.put(Integer.class, new IntegerWriter());
        typeStrategy.put(Long.class, new LongWriter());
        typeStrategy.put(Short.class, new ShortWriter());
        typeStrategy.put(Boolean.class, new BooleanWriter());
        typeStrategy.put(Byte.class, new ByteWriter());
        typeStrategy.put(Character.class, new CharacterWriter());
        typeStrategy.put(int[].class, new IntArrayWriter());
        typeStrategy.put(boolean[].class, new BooleanArrayWriter());
        typeStrategy.put(long[].class, new LongArrayWriter());
        typeStrategy.put(short[].class, new ShortArrayWriter());
        typeStrategy.put(byte[].class, new ByteArrayWriter());
        typeStrategy.put(float[].class, new FloatArrayWriter());
        typeStrategy.put(double[].class, new DoubleArrayWriter());
        typeStrategy.put(char[].class, new CharArrayWriter());
        typeStrategy.put(String[].class, new StringArrayWriter());
        typeStrategy.put(ArrayList.class, new ArrayListWriter());
        typeStrategy.put(Date.class, new DateWriter());
        typeStrategy.put(java.sql.Date.class, new DateWriter());
    }
    
    public boolean isWriteEnumName()
    {
        return writeEnumName;
    }
    
    public void setWriteEnumName(boolean writeEnumName)
    {
        this.writeEnumName = writeEnumName;
    }
    
    public boolean isUseTracker()
    {
        return useTracker;
    }
    
    public void setUseTracker(boolean useTracker)
    {
        this.useTracker = useTracker;
    }
    
    public boolean containsStrategyType(Class<?> type)
    {
        return typeStrategy.containsKey(type);
    }
    
    public JsonWriter getWriter(Class<?> type)
    {
        writer = typeStrategy.get(type);
        if (writer == null)
        {
            writer = WriterContext.getWriter(type, this);
            typeStrategy.put(type, writer);
            return writer;
        }
        else
        {
            return writer;
        }
    }
    
    public void addTypeStrategy(Class<?> ckass, JsonWriter jsonWriter)
    {
        typeStrategy.put(ckass, jsonWriter);
    }
    
    public void addTrackerType(Class<?> ckass, JsonWriter jsonWriter)
    {
        trackerType.put(ckass, jsonWriter);
    }
    
    public boolean containsTrackerType(Class<?> ckass)
    {
        return trackerType.containsKey(ckass);
    }
    
    public JsonWriter getTrackerType(Class<?> ckass)
    {
        return trackerType.get(ckass);
    }
    
    public boolean containsStrategyField(String fieldName)
    {
        return fieldStrategy.containsKey(fieldName);
    }
    
    public JsonWriter getWriterByField(String fieldName)
    {
        return fieldStrategy.get(fieldName);
    }
    
    public void addFieldStrategy(String fieldName, JsonWriter writer)
    {
        fieldStrategy.put(fieldName, writer);
        
    }
    
    public void addIgnoreField(String fieldName)
    {
        ignoreFields.add(fieldName);
    }
    
    public boolean ignore(String fieldName)
    {
        return ignoreFields.contains(fieldName);
    }
    
    public void addRenameField(String originName, String rename)
    {
        renameFields.put(originName, rename);
    }
    
    public String getRename(String fieldName)
    {
        return renameFields.get(fieldName);
    }
    
    public boolean containsRename(String fieldName)
    {
        return renameFields.containsKey(fieldName);
    }
    
    /**
     * 使用策略输出这个对象。第一次调用该方法就会将策略固化，后续在增加策略无效
     * 
     * @param entity
     * @return
     */
    public String write(Object entity)
    {
        StringCache cache = cacheLocal.get();
        cache.clear();
        if (useTracker)
        {
            Tracker tracker = trackerLocal.get();
            tracker.clear();
            tracker.put(entity, "$", false);
            getWriter(entity.getClass()).write(entity, cache, null, tracker);
        }
        else
        {
            getWriter(entity.getClass()).write(entity, cache, null, null);
        }
        return cache.toString();
    }
}
