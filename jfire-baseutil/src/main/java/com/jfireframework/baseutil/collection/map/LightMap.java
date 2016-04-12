package com.jfireframework.baseutil.collection.map;

import java.lang.reflect.Array;

/**
 * 无限制，简易Map。内容放入时不会执行查重检查，直接放入。不提供根据key取得value方法。
 * 不会自动增长，初始时便要设定容量。主要用于数据的简单存放。
 * 
 * @author 林斌（windfire@zailanghua.com）
 *         
 */
public class LightMap<K, V>
{
    private int           count = 0;
    private Entry<K, V>[] entries;
                          
    /**
     * 指定简易map的初始容量
     * 
     * @param size
     */
    @SuppressWarnings("unchecked")
    public LightMap(int size)
    {
        entries = new Entry[size];
    }
    
    public LightMap()
    {
        this(10);
    }
    
    /**
     * 返回当前的存入的键值对个数
     * 
     * @return
     */
    public int getCount()
    {
        return count;
    }
    
    /**
     * 将键值对放入简易map
     * 
     * @param key
     * @param value
     */
    public void put(K key, V value)
    {
        entries[count] = new Entry<K, V>(key, value);
        count++;
    }
    
    /**
     * 返回存储键值对的数组
     * 
     * @return
     */
    public Entry<K, V>[] getEntries()
    {
        return entries;
    }
    
    /**
     * 根据下标找到对应的entry。返回其key
     * 
     * @param index
     * @return
     */
    public K getKeyByIndex(int index)
    {
        return entries[index].getKey();
    }
    
    /**
     * 根据下标找到对应的entry。返回其value
     * 
     * @param index
     * @return
     */
    public V getValueByIndex(int index)
    {
        return entries[index].getValue();
    }
    
    /**
     * 返回map中key组成的数组
     * 注意：这里有一个暗示，返回的key数组和value数组在值上是一一对应的
     * 
     * @param keyClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public K[] toKeyArray(Class<K> keyClass)
    {
        K[] array = (K[]) Array.newInstance(keyClass, count);
        for (int i = 0; i < count; i++)
        {
            array[i] = entries[i].getKey();
        }
        return array;
    }
    
    /**
     * 返回map中的value组成的数组
     * 注意：这里有一个暗示，value数组的内容顺序和key数组的内容顺序是一致的
     * 
     * @param valueClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public V[] toValueArray(Class<V> valueClass)
    {
        V[] array = (V[]) Array.newInstance(valueClass, count);
        for (int i = 0; i < count; i++)
        {
            array[i] = entries[i].getValue();
        }
        return array;
    }
    
}
