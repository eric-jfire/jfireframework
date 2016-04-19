package com.jfireframework.codejson.function;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.codejson.annotation.JsonIgnore;
import com.jfireframework.codejson.function.impl.write.IteratorWriter;
import com.jfireframework.codejson.function.impl.write.MapWriter;
import com.jfireframework.codejson.function.impl.write.StrategyMapWriter;
import com.jfireframework.codejson.function.impl.write.WriterAdapter;
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
import com.jfireframework.codejson.methodinfo.MethodInfoBuilder;
import com.jfireframework.codejson.methodinfo.WriteMethodInfo;
import com.jfireframework.codejson.tracker.Tracker;
import com.jfireframework.codejson.util.MethodComparator;
import com.jfireframework.codejson.util.NameTool;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class WriterContext
{
    private static ConcurrentHashMap<Class<?>, JsonWriter> writerMap = new ConcurrentHashMap<Class<?>, JsonWriter>();
    private static ClassPool                               classPool = ClassPool.getDefault();
    private static Logger                                  logger    = ConsoleLogFactory.getLogger();
    static
    {
        ClassPool.doPruning = true;
        classPool.insertClassPath(new ClassClassPath(WriterContext.class));
        classPool.importPackage("com.jfireframework.codejson.function");
        classPool.importPackage("com.jfireframework.codejson");
        classPool.importPackage("com.jfireframework.codejson.tracker");
        classPool.importPackage("java.util");
        classPool.importPackage("com.jfireframework.baseutil.collection");
    }
    
    static
    {
        writerMap.put(String.class, new StringWriter());
        writerMap.put(Double.class, new DoubleWriter());
        writerMap.put(Float.class, new FloatWriter());
        writerMap.put(Integer.class, new IntegerWriter());
        writerMap.put(Long.class, new LongWriter());
        writerMap.put(Short.class, new ShortWriter());
        writerMap.put(Boolean.class, new BooleanWriter());
        writerMap.put(Byte.class, new ByteWriter());
        writerMap.put(Character.class, new CharacterWriter());
        writerMap.put(int[].class, new IntArrayWriter());
        writerMap.put(boolean[].class, new BooleanArrayWriter());
        writerMap.put(long[].class, new LongArrayWriter());
        writerMap.put(short[].class, new ShortArrayWriter());
        writerMap.put(byte[].class, new ByteArrayWriter());
        writerMap.put(float[].class, new FloatArrayWriter());
        writerMap.put(double[].class, new DoubleArrayWriter());
        writerMap.put(char[].class, new CharArrayWriter());
        writerMap.put(String[].class, new StringArrayWriter());
        writerMap.put(ArrayList.class, new ArrayListWriter());
        writerMap.put(Date.class, new DateWriter());
        writerMap.put(java.sql.Date.class, new DateWriter());
    }
    
    public static void write(Object entity, StringCache cache)
    {
        if (entity == null)
        {
            cache.append("null");
        }
        else
        {
            getWriter(entity.getClass()).write(entity, cache, null, null);
        }
    }
    
    public static JsonWriter getWriter(Class<?> ckass)
    {
        JsonWriter writer = writerMap.get(ckass);
        if (writer == null)
        {
            try
            {
                writer = (JsonWriter) createWriter(ckass, null).newInstance();
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
            writerMap.putIfAbsent(ckass, writer);
        }
        return writer;
    }
    
    protected static JsonWriter getWriter(Class<?> ckass, WriteStrategy strategy)
    {
        // 这个方法每次都必须重新生成新的输出类。实际上这个方法并不是给用户直接调用的。而是给策略模式进行writer生成的。
        // 如果在这里使用writerMap进行判断，就会导致一种情况：前面已经进行过该类的输出生成了，现在有个新的策略，策略就无法生效。因为不会生成新的输出类
        Class<?> result = createWriter(ckass, strategy);
        try
        {
            Constructor<?> constructor = result.getConstructor(WriteStrategy.class);
            return (JsonWriter) constructor.newInstance(strategy);
        }
        catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 创建一个输出类cklas的jsonwriter
     * 
     * @param cklas
     * @return
     */
    private static Class<?> createWriter(Class<?> cklas, WriteStrategy strategy)
    {
        if (cklas.isArray())
        {
            return buildArrayWriter(cklas);
        }
        else if (Iterable.class.isAssignableFrom(cklas))
        {
            return IteratorWriter.class;
        }
        else if (Map.class.isAssignableFrom(cklas))
        {
            if (strategy == null)
            {
                return MapWriter.class;
            }
            else
            {
                return StrategyMapWriter.class;
            }
        }
        else
        {
            StringCache stringCache = new StringCache();
            stringCache.append("{\nStringCache cache = (StringCache)$2;\n");
            String entityName = "entity" + System.nanoTime();
            stringCache.append(cklas.getName() + " " + entityName + " =(" + cklas.getName() + " )$1;\n");
            stringCache.append("cache.append('{');\n");
            Method[] methods = ReflectUtil.listGetMethod(cklas);
            Arrays.sort(methods, new MethodComparator());
            for (Method each : methods)
            {
                if (needIgnore(each, strategy))
                {
                    continue;
                }
                WriteMethodInfo methodInfo = MethodInfoBuilder.buildWriteMethodInfo(each, strategy, entityName);
                stringCache.append(methodInfo.getOutput());
            }
            stringCache.append("if(cache.isCommaLast())\n{\n\tcache.deleteLast();\n}\n");
            stringCache.append("cache.append('}');\n}");
            try
            {
                CtClass implClass = classPool.makeClass("JsonWriter_" + cklas.getSimpleName() + "_" + System.nanoTime());
                implClass.setSuperclass(classPool.getCtClass(WriterAdapter.class.getName()));
                if (strategy != null)
                {
                    implClass.setName("JsonWriter_Strategy_" + cklas.getSimpleName() + '_' + System.nanoTime());
                    createStrategyConstructor(implClass);
                }
                CtClass ObjectCc = classPool.get(Object.class.getName());
                CtClass cacheCc = classPool.get(StringCache.class.getName());
                CtClass trackerCc = classPool.get(Tracker.class.getName());
                CtMethod method = new CtMethod(CtClass.voidType, "write", new CtClass[] { ObjectCc, cacheCc, ObjectCc, trackerCc }, implClass);
                logger.trace("{}创建的输出方法是\r{}", implClass.getName(), stringCache.toString());
                method.setBody(stringCache.toString());
                implClass.addMethod(method);
                implClass.rebuildClassFile();
                return implClass.toClass(cklas.getClassLoader(), null);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    private static void createStrategyConstructor(CtClass ckass) throws CannotCompileException, NotFoundException
    {
        CtField ctField = new CtField(classPool.get(WriteStrategy.class.getName()), "writeStrategy", ckass);
        ctField.setModifiers(Modifier.PUBLIC);
        ckass.addField(ctField);
        CtConstructor constructor = new CtConstructor(new CtClass[] { classPool.get(WriteStrategy.class.getName()) }, ckass);
        constructor.setBody("{this.writeStrategy = $1;}");
        ckass.addConstructor(constructor);
    }
    
    private static boolean needIgnore(Method method, WriteStrategy strategy)
    {
        String fieldName = ReflectUtil.getFieldNameFromMethod(method);
        if (method.isAnnotationPresent(JsonIgnore.class) && method.getAnnotation(JsonIgnore.class).force())
        {
            return true;
        }
        if (strategy != null)
        {
            if (strategy.ignore(method.getDeclaringClass().getName() + '.' + fieldName))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        if (method.isAnnotationPresent(JsonIgnore.class))
        {
            return true;
        }
        try
        {
            Field field = method.getDeclaringClass().getDeclaredField(fieldName);
            if (field.isAnnotationPresent(JsonIgnore.class))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (NoSuchFieldException | SecurityException e)
        {
            return false;
        }
    }
    
    private static Class<?> buildArrayWriter(Class<?> targetClass)
    {
        Class<?> rootType = targetClass;
        int dim = 0;
        while (rootType.isArray())
        {
            dim++;
            rootType = rootType.getComponentType();
        }
        String rootName = rootType.getName();
        String str = "{\n\t" + NameTool.buildDimTypeName(rootName, dim) + " array" + dim + " = (" + NameTool.buildDimTypeName(rootName, dim) + ")$1;\n";
        str += "\tStringCache cache = (StringCache)$2;\n";
        str += "\tcache.append('[');\n";
        str += "\tint l" + dim + " = array" + dim + ".length;\n";
        String bk = "\t";
        for (int i = dim; i > 1; i--)
        {
            int next = i - 1;
            str += bk + "for(int i" + i + " = 0;i" + i + "<l" + i + ";i" + i + "++)\n";
            str += bk + "{\n";
            str += bk + "\tcache.append('[');\n";
            str += bk + "\t" + NameTool.buildDimTypeName(rootName, next) + " array" + next + " = array" + i + "[i" + i + "];\n";
            str += bk + "\tint l" + next + " =  array" + next + ".length;\n";
            bk += "\t";
        }
        str += bk + "for(int i1=0;i1<l1;i1++)\n";
        str += bk + "{\n";
        str += bk + "\tWriterContext.write(array1[i1],cache);\n";
        str += bk + "\tcache.append(',');\n";
        str += bk + "}\n";
        for (int i = dim; i > 1; i--)
        {
            str += bk + "if(cache.isCommaLast()){cache.deleteLast();}\n";
            str += bk + "cache.append(']');\n";
            str += bk + "cache.append(',');\n";
            bk = bk.substring(0, bk.length() - 1);
            str += bk + "}\n";
        }
        str += bk + "if(cache.isCommaLast()){cache.deleteLast();}\n";
        str += bk + "cache.append(']');\n";
        str += "}";
        try
        {
            CtClass implClass = classPool.makeClass("JsonWriter_" + targetClass.getSimpleName() + "_" + System.currentTimeMillis());
            CtClass interfaceCtClass = classPool.getCtClass(JsonWriter.class.getName());
            implClass.setInterfaces(new CtClass[] { interfaceCtClass });
            CtClass ObjectCc = classPool.get(Object.class.getName());
            CtClass cacheCc = classPool.get(StringCache.class.getName());
            CtMethod method = new CtMethod(CtClass.voidType, "write", new CtClass[] { ObjectCc, cacheCc, ObjectCc }, implClass);
            method.setBody(str);
            implClass.addMethod(method);
            implClass.rebuildClassFile();
            return implClass.toClass();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void putwriter(Class<?> cklass, JsonWriter jsonWriter)
    {
        writerMap.put(cklass, jsonWriter);
    }
}
