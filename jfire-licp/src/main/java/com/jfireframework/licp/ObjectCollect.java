package com.jfireframework.licp;

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
    public int putIfAbsent(Object value)
    {
        int index = indexOf(value);
        if (index == -1)
        {
            ensureCapacity(1);
            array[count] = value;
            count++;
            return 0 - count + 1;
        }
        else
        {
            return index;
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
    
}
