package com.jfireframework.codejson.function;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.codejson.annotation.JsonIgnore;
import com.jfireframework.codejson.function.impl.read.DateReader;
import com.jfireframework.codejson.function.impl.read.FileReader;
import com.jfireframework.codejson.function.impl.read.ObjectReader;
import com.jfireframework.codejson.function.impl.read.array.BooleanArrayReader;
import com.jfireframework.codejson.function.impl.read.array.ByteArrayReader;
import com.jfireframework.codejson.function.impl.read.array.CharArrayReader;
import com.jfireframework.codejson.function.impl.read.array.DoubleArrayReader;
import com.jfireframework.codejson.function.impl.read.array.FloatArrayReader;
import com.jfireframework.codejson.function.impl.read.array.IntArrayReader;
import com.jfireframework.codejson.function.impl.read.array.LongArrayReader;
import com.jfireframework.codejson.function.impl.read.array.ShortArrayReader;
import com.jfireframework.codejson.function.impl.read.array.StringArrayReader;
import com.jfireframework.codejson.function.impl.read.wrapper.BooleanReader;
import com.jfireframework.codejson.function.impl.read.wrapper.ByteReader;
import com.jfireframework.codejson.function.impl.read.wrapper.CharacterReader;
import com.jfireframework.codejson.function.impl.read.wrapper.DoubleReader;
import com.jfireframework.codejson.function.impl.read.wrapper.FloatReader;
import com.jfireframework.codejson.function.impl.read.wrapper.IntegerReader;
import com.jfireframework.codejson.function.impl.read.wrapper.LongReader;
import com.jfireframework.codejson.function.impl.read.wrapper.ShortReader;
import com.jfireframework.codejson.methodinfo.MethodInfoBuilder;
import com.jfireframework.codejson.methodinfo.ReadMethodInfo;
import com.jfireframework.codejson.util.MethodComparator;
import com.jfireframework.codejson.util.NameTool;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

public class ReaderContext
{
    private static Map<Type, JsonReader> readerMap  = new ConcurrentHashMap<Type, JsonReader>();
    private static ClassPool             classPool;
    private static Set<Class<?>>         wrapperSet = new HashSet<Class<?>>();
    private static Logger                logger     = ConsoleLogFactory.getLogger();
    static
    {
        wrapperSet.add(String.class);
        wrapperSet.add(Boolean.class);
        wrapperSet.add(Integer.class);
        wrapperSet.add(Long.class);
        wrapperSet.add(Short.class);
        wrapperSet.add(Float.class);
        wrapperSet.add(Double.class);
        wrapperSet.add(Short.class);
        wrapperSet.add(Byte.class);
        wrapperSet.add(Character.class);
        wrapperSet.equals(String.class);
    }
    
    public static void initClassPool(ClassLoader classLoader)
    {
        ClassPool.doPruning = true;
        classPool = new ClassPool();
        classPool.appendClassPath(new ClassClassPath(ReaderContext.class));
        classPool.importPackage("com.jfireframework.codejson.function");
        classPool.importPackage("com.jfireframework.codejson");
        classPool.importPackage("java.util");
        classPool.importPackage("com.jfireframework.baseutil.collection");
        if (classLoader != null)
        {
            classPool.insertClassPath(new LoaderClassPath(classLoader));
        }
    }
    
    static
    {
        initClassPool(null);
    }
    
    static
    {
        readerMap.put(Boolean.class, new BooleanReader());
        readerMap.put(Byte.class, new ByteReader());
        readerMap.put(Character.class, new CharacterReader());
        readerMap.put(Double.class, new DoubleReader());
        readerMap.put(Float.class, new FloatReader());
        readerMap.put(Integer.class, new IntegerReader());
        readerMap.put(Long.class, new LongReader());
        readerMap.put(Short.class, new ShortReader());
        readerMap.put(int[].class, new IntArrayReader());
        readerMap.put(byte[].class, new ByteArrayReader());
        readerMap.put(boolean[].class, new BooleanArrayReader());
        readerMap.put(char[].class, new CharArrayReader());
        readerMap.put(long[].class, new LongArrayReader());
        readerMap.put(short[].class, new ShortArrayReader());
        readerMap.put(float[].class, new FloatArrayReader());
        readerMap.put(double[].class, new DoubleArrayReader());
        readerMap.put(String[].class, new StringArrayReader());
        readerMap.put(Object.class, new ObjectReader());
        readerMap.put(Date.class, new DateReader());
        readerMap.put(File.class, new FileReader());
    }
    
    public static Object read(Type entityType, Object value)
    {
        return getReader(entityType).read(entityType, value);
    }
    
    public static JsonReader getReader(Type ckass)
    {
        JsonReader reader = readerMap.get(ckass);
        if (reader == null)
        {
            try
            {
                reader = (JsonReader) createReader(ckass, null).newInstance();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            readerMap.put(ckass, reader);
        }
        return reader;
    }
    
    protected static JsonReader getReader(Type ckass, ReadStrategy readStrategy)
    {
        try
        {
            return (JsonReader) createReader(ckass, readStrategy).getConstructor(ReadStrategy.class).newInstance(readStrategy);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 创建一个输出类cklas的jsonwriter
     * 
     * @param type
     * @return
     */
    protected static Class<?> createReader(Type type, ReadStrategy strategy)
    {
        String body = null;
        if (type instanceof Class)
        {
            Class<?> tmp = (Class<?>) type;
            if (tmp.isArray())
            {
                body = buildArrayReader(tmp);
            }
            else
            {
                StringCache stringCache = new StringCache();
                stringCache.append("{\n" + tmp.getName() + " entity = new " + tmp.getName() + "();\n");
                stringCache.append("JsonObject json = (JsonObject)$2;\n");
                Method[] methods = ReflectUtil.listSetMethod(tmp);
                Arrays.sort(methods, new MethodComparator());
                for (Method each : methods)
                {
                    if (needIgnore(each, strategy))
                    {
                        continue;
                    }
                    ReadMethodInfo methodInfo = MethodInfoBuilder.buildReadMethodInfo(each, strategy);
                    stringCache.append(methodInfo.getOutput());
                }
                stringCache.append("return entity;\n}");
                body = stringCache.toString();
            }
            return buildClass(body, tmp, strategy);
        }
        else if (type instanceof ParameterizedType)
        {
            Class<?> targetClass = (Class<?>) ((ParameterizedType) type).getRawType();
            if (targetClass.isInterface() || Modifier.isAbstract(targetClass.getModifiers()))
            {
                throw new RuntimeException("反序列化需要具体的类信息，不能是接口或者是抽象类");
            }
            else if (Collection.class.isAssignableFrom(targetClass))
            {
                Type paramType = ((ParameterizedType) type).getActualTypeArguments()[0];
                if (type instanceof WildcardType)
                {
                    throw new RuntimeException("反序列化不能使用？泛型参数");
                }
                body = buildCollectionReader(targetClass, paramType);
            }
            else if (Map.class.isAssignableFrom(targetClass))
            {
                Type keyType = ((ParameterizedType) type).getActualTypeArguments()[0];
                Type valueType = ((ParameterizedType) type).getActualTypeArguments()[1];
                if (keyType instanceof WildcardType || valueType instanceof WildcardType)
                {
                    throw new RuntimeException("反序列化不能使用？泛型参数");
                }
                body = buildMapReader(targetClass, keyType, valueType);
            }
            return buildClass(body, targetClass, strategy);
        }
        else if (type instanceof GenericArrayType)
        {
            Type root = type;
            while (root instanceof GenericArrayType)
            {
                root = ((GenericArrayType) root).getGenericComponentType();
            }
            if (root instanceof WildcardType)
            {
                throw new RuntimeException("反序列化需要确定的信息，不能使用？泛型参数");
            }
            body = buildArrayReader(type);
            if (root instanceof ParameterizedType)
            {
                root = ((ParameterizedType) root).getRawType();
            }
            return buildClass(body, (Class<?>) root, strategy);
        }
        else if (type instanceof WildcardType)
        {
            throw new RuntimeException("反序列化需要确定的信息，不能使用？泛型参数");
        }
        else
        {
            throw new RuntimeException("未知情况");
        }
    }
    
    private static Class<?> buildClass(String body, Class<?> targetClass, ReadStrategy strategy)
    {
        try
        {
            CtClass implClass = classPool.makeClass("JsonReader_" + targetClass.getSimpleName() + "_" + System.nanoTime());
            CtClass interfaceCtClass = classPool.getCtClass(JsonReader.class.getName());
            if (strategy != null)
            {
                implClass.setName("JsonRead_Strategy_" + targetClass.getSimpleName() + '_' + System.nanoTime());
                createStrategyConstructor(implClass);
            }
            implClass.setInterfaces(new CtClass[] { interfaceCtClass });
            CtMethod method = new CtMethod(classPool.get(Object.class.getName()), "read", new CtClass[] { classPool.get(Type.class.getName()), classPool.get(Object.class.getName()) }, implClass);
            logger.trace("为目标类{}生成的读取json的方法体是\n{}\n", targetClass.getName(), body);
            method.setBody(body);
            implClass.addMethod(method);
            implClass.stopPruning(false);
            implClass.rebuildClassFile();
            return implClass.toClass();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private static void createStrategyConstructor(CtClass ckass) throws CannotCompileException, NotFoundException
    {
        CtField ctField = new CtField(classPool.get(ReadStrategy.class.getName()), "readStrategy", ckass);
        ctField.setModifiers(Modifier.PUBLIC);
        ckass.addField(ctField);
        CtConstructor constructor = new CtConstructor(new CtClass[] { classPool.get(ReadStrategy.class.getName()) }, ckass);
        constructor.setBody("{this.readStrategy = $1;}");
        ckass.addConstructor(constructor);
    }
    
    private static String buildArrayReader(Type targetClass)
    {
        String rootName = null;
        int dim = 0;
        if (targetClass instanceof Class)
        {
            Class<?> rootType = (Class<?>) targetClass;
            dim = 0;
            while (rootType.isArray())
            {
                dim++;
                rootType = rootType.getComponentType();
            }
            rootName = rootType.getName();
        }
        else
        {
            Type root = targetClass;
            dim = 0;
            while (root instanceof GenericArrayType)
            {
                dim++;
                root = ((GenericArrayType) root).getGenericComponentType();
            }
            if (root instanceof ParameterizedType)
            {
                root = ((ParameterizedType) root).getRawType();
                if (root instanceof Class)
                {
                    rootName = ((Class<?>) root).getName();
                }
                else
                {
                    throw new RuntimeException("反序列化需要的类定义信息不明确");
                }
            }
            else
            {
                rootName = ((Class<?>) root).getName();
            }
        }
        String str = "{\n\tJsonArray jsonArray" + dim + " = ((JsonArray)$2);\n";
        str += "\tint l" + dim + " = jsonArray" + dim + ".size();\n";
        str += "\t" + NameTool.buildDimTypeName(rootName, dim) + " array" + dim + " = " + NameTool.buildNewDimTypeName(rootName, dim, "l" + dim) + ";\n";
        str += "\t" + "for(int i" + dim + " = 0;i" + dim + "<l" + dim + ";i" + dim + "++)\n";
        str += "\t{\n";
        String bk = "\t\t";
        for (int i = dim - 1; i > 0; i--)
        {
            str += bk + "JsonArray jsonArray" + i + " =jsonArray" + (i + 1) + ".getJsonArray(i" + (i + 1) + ");\n";
            str += bk + "int l" + i + " = jsonArray" + i + ".size();\n";
            str += bk + NameTool.buildDimTypeName(rootName, i) + " array" + i + " = " + NameTool.buildNewDimTypeName(rootName, i, "l" + i) + ";\n";
            str += bk + "for(int i" + i + " = 0;i" + i + "<l" + i + ";i" + i + "++)\n";
            str += bk + "{\n";
            bk += "\t";
        }
        if (targetClass instanceof Class)
        {
            str += bk + "array1[i1] = (" + rootName + ")ReaderContext.read(" + rootName + ".class,jsonArray1.get(i1));\n";
        }
        else
        {
            throw new RuntimeException("javassist不支持泛型参数，无法完成该对象的");
            // str += bk + "Type type = new TypeUtil<" +
            // String.valueOf(component) + ">(){}.getType();\n";
            // str += bk + "array1[i1] = (" + rootName +
            // ")ReaderContext.read(type,jsonArray1.get(i1));\n";
        }
        for (int i = 1; i <= dim - 1; i++)
        {
            bk = bk.substring(0, bk.length() - 1);
            str += bk + "}\n";
            str += bk + "array" + (i + 1) + "[i" + (i + 1) + "] = array" + i + ";\n";
        }
        str += "\t}\n";
        str += "\treturn array" + dim + ";\n";
        str += "}";
        return str;
        
    }
    
    private static String buildCollectionReader(Class<?> collectionClass, Type paramType)
    {
        String str = "{\n\t" + collectionClass.getName() + " collection = new " + collectionClass.getName() + "();\n";
        str += "\tJsonArray jsonArray = (JsonArray)$2;\n";
        str += "\tint size = jsonArray.size();\n";
        str += "\tfor(int i=0;i<size;i++)\n";
        str += "\t{\n";
        if (wrapperSet.contains(paramType))
        {
            str += "\t\tcollection.add(jsonArray.getW" + ((Class<?>) paramType).getSimpleName() + "(i));\n";
        }
        else if (paramType instanceof Class)
        {
            str += "\t\tcollection.add(ReaderContext.read(" + ((Class<?>) paramType).getName() + ".class,jsonArray.get(i)));\n";
        }
        else if (paramType instanceof Type)
        {
            str += "\t\tcollection.add(ReaderContext.read(new TypeUtil<" + String.valueOf(paramType) + ">(){}.getType(),jsonArray.get(i));\n";
        }
        else
        {
            throw new RuntimeException("未知错误");
        }
        str += "\t}\n";
        str += "\treturn collection;\n";
        str += "}";
        return str;
    }
    
    private static String buildMapReader(Class<?> mapClass, Type keyType, Type valueType)
    {
        String str = "{\n\t" + mapClass.getName() + " map = new " + mapClass.getName() + "();\n";
        str += "\tJsonObject jsonObject = (JsonObject)$2;\n";
        str += "\tint size = jsonObject.size();\n";
        str += "\tIterator it = jsonObject.entrySet().iterator();\n";
        str += "\tObject key = null;\n";
        str += "\tObject value = null;\n";
        str += "\twhile(it.hasNext())\n";
        str += "\t{\n";
        str += "\t\tjava.util.Map.Entry each = (java.util.Map.Entry)it.next();\n";
        if (keyType instanceof Class)
        {
            if (((Class<?>) keyType).equals(String.class))
            {
                str += "\t\tkey = (String)each.getKey();\n";
            }
            else if (((Class<?>) keyType).equals(Character.class))
            {
                str += "\t\tkey = ((String)each.getKey()).charAt(0);\n";
            }
            else if (wrapperSet.contains(keyType))
            {
                str += "\t\tkey = " + ((Class<?>) keyType).getName() + ".valueOf((String)each.getKey());\n";
            }
            else
            {
                str += "\t\tkey = ReaderContext.read(" + ((Class<?>) keyType).getName() + ".class,(String)each.getKey());\n";
            }
        }
        else
        {
            throw new RuntimeException("暂时超出处理逻辑，请发邮件给作者eric@jfire.cn");
        }
        if (valueType instanceof Class)
        {
            Class<?> tmp = (Class<?>) valueType;
            if (tmp.equals(String.class))
            {
                str += "\t\tvalue = (String)each.getValue();\n";
            }
            else if (tmp.equals(Character.class))
            {
                str += "\t\tvalue = ((String)each.getValue()).charAt(0);\n";
            }
            else if (wrapperSet.contains(tmp))
            {
                str += "\t\tvalue = jsonObject.getW" + tmp.getSimpleName() + "(each.getKey());\n";
            }
            else
            {
                str += "\t\tvalue = ReaderContext.read(" + tmp.getName() + ".class,each.getValue());\n";
            }
        }
        else
        {
            str += "\t\tvalue = ReaderContext.read(new TypeUtil<" + String.valueOf(valueType) + ">(){}.getType(),each.getValue());\n";
        }
        str += "\t\tmap.put(key,value);\n";
        str += "\t}\n";
        str += "\treturn map;";
        str += "}";
        return str;
    }
    
    public static void putReader(Class<?> ckass, JsonReader jsonReader)
    {
        readerMap.put(ckass, jsonReader);
    }
    
    private static boolean needIgnore(Method method, ReadStrategy strategy)
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
        catch (Exception e)
        {
            return false;
        }
    }
}
