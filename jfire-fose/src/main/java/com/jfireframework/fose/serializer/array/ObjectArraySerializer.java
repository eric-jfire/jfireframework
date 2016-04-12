package com.jfireframework.fose.serializer.array;

import java.lang.reflect.Array;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.BeanSerializerFactory;
import com.jfireframework.fose.util.DimensionUtil;

public class ObjectArraySerializer extends AbstractArraySerializer
{
    
    @Override
    public void getObjects(Object src, ObjectCollect collect)
    {
        if (collect.add(src))
        {
            putEachSingleInCollectFromArray(collect, src, DimensionUtil.getDimByComponent(src.getClass()));
        }
    }
    
    /**
     * 对数组不断进行分解，直到将数组中每一个单一元素均放入collect中
     * 
     * @param collection
     * @param array
     * @param dimension
     */
    private void putEachSingleInCollectFromArray(ObjectCollect collect, Object array, int dimension)
    {
        if (array == null)
        {
            return;
        }
        Object[] value = (Object[]) array;
        int length = value.length;
        if (dimension == 1)
        {
            for (int i = 0; i < length; i++)
            {
                if (value[i] != null)
                {
                    BeanSerializerFactory.getSerializer(value[i].getClass()).getObjects(value[i], collect);
                }
            }
        }
        else
        {
            
            for (int i = 0; i < length; i++)
            {
                putEachSingleInCollectFromArray(collect, value[i], dimension - 1);
            }
        }
    }
    
    @Override
    protected void writeOneDimensionMember(Object src, ByteBuf<?> buf, boolean first, ObjectCollect collect)
    {
        Object[] array = (Object[]) src;
        if (first == false)
        {
            buf.writeInt(array.length);
        }
        for (Object each : array)
        {
            buf.writeInt(collect.indexOf(each));
        }
        
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    protected Object readOneDimensionMember(Object target, Integer length, ByteBuf<?> buf, ObjectCollect collect, Class rootType)
    {
        Object[] array = target == null ? (Object[]) Array.newInstance(rootType, length) : (Object[]) target;
        length = array.length;
        for (int i = 0; i < length; i++)
        {
            array[i] = collect.get(buf.readInt());
        }
        return array;
    }
    
}
