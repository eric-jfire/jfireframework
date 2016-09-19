package com.jfireframework.baseutil.collection;

import java.util.Collection;

/**
 * 对象实例收集器，具备自动的容量扩展功能。
 * 不存放相同对象（内存地址相同，同一对象的相同内容的不同实例被认为是不同对象）
 * 
 * @author linbin
 * 
 */
public class ObjectCollect
{
    private Object[] array;
    private int      size;
    private int      count = 0;
    
    /**
     * 以size大小初始化收集器
     * 
     * @param size
     */
    public ObjectCollect(int size)
    {
        this.size = size;
        array = new Object[size];
        count = 0;
    }
    
    /**
     * 默认构造方法，初始容量为50
     */
    public ObjectCollect()
    {
        this(50);
    }
    
    /**
     * 向对象收集器中添加对象实例，如果该实例已经添加过，则返回false，否则返回真 该添加对比只进行对象实例内存地址判断，而不进行相等性判断
     * 
     * @param value
     * @return
     */
    public boolean add(Object value)
    {
        if (indexOf(value) == -1)
        {
            ensureCapacity(1);
            array[count] = value;
            count++;
            return true;
        }
        return false;
    }
    
    /**
     * 从集合中删除该元素
     * 
     * @param value
     */
    public void remove(Object value)
    {
        for (int i = 0; i < count; i++)
        {
            if (array[i] == value)
            {
                // 如果删除该元素，则该位置以末尾元素填充。这样避免整个数组移动
                count--;
                array[i] = array[count];
                break;
            }
        }
    }
    
    /**
     * 向对象收集器内添加对象，而不进行容量检查。
     * 加入的时候会检查该对象是否已经在收集器内。如果在的话，则不加入，并且返回false
     * 成功则返回true
     * 
     * @param value
     * @return
     */
    public boolean addWithoutEnsureCapacity(Object value)
    {
        if (indexOf(value) == -1)
        {
            array[count] = value;
            count++;
            return true;
            
        }
        return false;
    }
    
    /**
     * 将参数集合内的对象添加到对象收集器中
     * 
     * @param collection
     */
    public void addAll(Collection<Object> collection)
    {
        ensureCapacity(collection.size());
        for (Object each : collection)
        {
            addWithoutEnsureCapacity(each);
        }
    }
    
    /**
     * 像收集器中放入数组，如果其中有重复对象，则重复对象不会加入
     * 
     * @param value
     */
    public void addAll(Object[] value)
    {
        ensureCapacity(value.length);
        for (Object each : value)
        {
            addWithoutEnsureCapacity(each);
        }
    }
    
    /**
     * 将另一个收集器内的对象加入本收集器内。重复的对象则不会加入
     * 
     * @param collect
     */
    public void addAll(ObjectCollect collect)
    {
        Object[] array = collect.getArray();
        ensureCapacity(collect.size);
        for (int i = 0; i < collect.getCount(); i++)
        {
            addWithoutEnsureCapacity(array[i]);
        }
    }
    
    /**
     * 确定收集器的剩余容量可以满足大小。如果不满足则自动扩容
     * 
     * @param needSize
     */
    public void ensureCapacity(int needSize)
    {
        if (size - count < needSize)
        {
            size = (size + needSize) * 2;
            Object[] tmp = new Object[size];
            System.arraycopy(array, 0, tmp, 0, count);
            array = tmp;
        }
    }
    
    /**
     * 返回某个对象在收集器中的位置，如果不存在，返回-1
     * 
     * @param value
     * @return
     */
    public int indexOf(Object value)
    {
        for (int i = 0; i < count; i++)
        {
            if (array[i] == value)
            {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 获取对应位置的对象
     * 
     * @param index
     * @return
     */
    public Object get(int index)
    {
        if (index < 0)
        {
            return null;
        }
        return array[index];
    }
    
    /**
     * 返回对象实例收集器中用来存储的数组，注意，该数组未被完全填满。如果需要完全填满的数组请使用toArray方法
     * 
     * @return
     */
    public Object[] getArray()
    {
        return array;
    }
    
    /**
     * 获取收集器当前收集的对象个数
     */
    public int getCount()
    {
        return count;
    }
    
    /**
     * 清空收集器
     */
    public void clear()
    {
        for (int i = 0; i < count; i++)
        {
            array[i] = null;
        }
        count = 0;
    }
    
    /**
     * 返回对象实例收集器中的对象组成的数组。注意该数组是填充完毕，拷贝出来的数组，如果想获得内部数组，请使用方法getArray
     * 
     * @return
     */
    public Object[] toArray()
    {
        Object[] result = new Object[count];
        System.arraycopy(array, 0, result, 0, count);
        return result;
    }
}
