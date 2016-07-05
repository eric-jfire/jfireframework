package com.jfireframework.licp.util;

import java.nio.charset.Charset;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class ClassNoWriter
{
    private static final Charset CHARSET = Charset.forName("utf8");
    
    /**
     * 如果类序号已经存在，写入一个int。int的最右位的bit是1.整个int右移一位得到的结果就是类的序号
     * 如果类序号为0.写入一个int。int的最右位的bit是0。整个int右移一位得到的结果是类名的byte数组长度
     * 
     * @param type
     * @param buf
     * @param licp
     */
    public static final void writeClassNo(Class<?> type, ByteBuf<?> buf, Licp licp)
    {
        int classNo = licp.indexOf(type);
        if (classNo == 0)
        {
            byte[] nameBytes = type.getName().getBytes(CHARSET);
            buf.writeInt(nameBytes.length << 1);
            buf.put(nameBytes);
        }
        else
        {
            classNo <<= 1;
            classNo |= 1;
            buf.writeInt(classNo);
        }
    }
    
    public static final Class<?> readClassNo(ByteBuf<?> buf, int classNo, Licp licp)
    {
        if ((classNo & 1) == 1)
        {
            classNo >>>= 1;
            return licp.loadClass(classNo);
        }
        else
        {
            classNo >>>= 1;
            byte[] nameBytes = new byte[classNo];
            buf.get(nameBytes, classNo);
            return licp.loadClass(new String(nameBytes, CHARSET));
        }
    }
}
