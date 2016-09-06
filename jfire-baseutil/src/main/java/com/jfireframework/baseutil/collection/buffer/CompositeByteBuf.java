package com.jfireframework.baseutil.collection.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedList;

public class CompositeByteBuf extends ByteBuf<Void>
{
    private static final RuntimeException unsupport = new RuntimeException("unsupport,it is a compositeBuf");
    private LinkedList<ByteBuf<?>>        bufs      = new LinkedList<ByteBuf<?>>();
    private ByteBuffer[]                  nioBuffers;
    
    public void addBuf(ByteBuf<?> buf)
    {
        bufs.add(buf);
    }
    
    public ByteBuffer[] nioBuffers()
    {
        if (nioBuffers == null)
        {
            LinkedList<ByteBuffer> buffers = new LinkedList<ByteBuffer>();
            for (ByteBuf<?> each : bufs)
            {
                buffers.add(each.nioBuffer());
            }
            nioBuffers = new ByteBuffer[buffers.size()];
            buffers.toArray(nioBuffers);
        }
        return nioBuffers;
    }
    
    @Override
    public void release()
    {
        for (ByteBuf<?> each : bufs)
        {
            each.release();
        }
        nioBuffers = null;
        bufs.clear();
    }
    
    @Override
    protected void _release()
    {
        throw unsupport;
    }
    
    @Override
    public ByteBuffer nioBuffer()
    {
        throw unsupport;
    }
    
    @Override
    protected void _put(ByteBuffer buffer, int length)
    {
        throw unsupport;
    }
    
    @Override
    protected void _put(int offset, byte b)
    {
        throw unsupport;
    }
    
    @Override
    protected void _put(byte[] content, int off, int len)
    {
        throw unsupport;
    }
    
    @Override
    protected void _expend(int size)
    {
        throw unsupport;
    }
    
    @Override
    public byte get(int index)
    {
        throw unsupport;
    }
    
    @Override
    public byte get()
    {
        throw unsupport;
    }
    
    @Override
    public byte[] toArray()
    {
        throw unsupport;
    }
    
    @Override
    public ByteBuf<Void> compact()
    {
        throw unsupport;
    }
    
    @Override
    protected void _get(byte[] content, int off, int length)
    {
        throw unsupport;
    }
    
    @Override
    public String toString(Charset charset, int length)
    {
        throw unsupport;
    }
    
    @Override
    protected void _put(ByteBuf<?> byteBuf, int length)
    {
        throw unsupport;
    }
    
    @Override
    protected void _writeInt(int index, int i)
    {
        throw unsupport;
    }
    
    @Override
    protected void _writeShort(int index, short s)
    {
        throw unsupport;
    }
    
    @Override
    protected void _writeLong(int index, long l)
    {
        throw unsupport;
    }
    
    @Override
    protected void _writeChar(int index, char c)
    {
        throw unsupport;
    }
    
    @Override
    protected void _writeBoolean(int index, boolean b)
    {
        throw unsupport;
    }
    
    @Override
    public int readInt()
    {
        throw unsupport;
    }
    
    @Override
    public int readInt(int index)
    {
        throw unsupport;
    }
    
    @Override
    public short readShort()
    {
        throw unsupport;
    }
    
    @Override
    public short readShort(int index)
    {
        throw unsupport;
    }
    
    @Override
    public long readLong()
    {
        throw unsupport;
    }
    
    @Override
    public long readLong(int index)
    {
        throw unsupport;
    }
    
    @Override
    public char readChar()
    {
        throw unsupport;
    }
    
    @Override
    public char readChar(int index)
    {
        throw unsupport;
    }
    
    @Override
    public float readFloat()
    {
        throw unsupport;
    }
    
    @Override
    public float readFloat(int index)
    {
        throw unsupport;
    }
    
    @Override
    public double readDouble()
    {
        throw unsupport;
    }
    
    @Override
    public double readDouble(int index)
    {
        throw unsupport;
    }
    
    @Override
    public boolean readBoolean()
    {
        throw unsupport;
    }
    
    @Override
    public String hexString()
    {
        throw unsupport;
    }
    
    @Override
    public void writePositive(int positive)
    {
        throw unsupport;
    }
    
    @Override
    public int readPositive()
    {
        throw unsupport;
    }
    
    @Override
    public ByteBuf<Void> writeVarint(int i)
    {
        throw unsupport;
    }
    
    @Override
    public int readVarint()
    {
        throw unsupport;
    }
    
    @Override
    public ByteBuf<Void> writeVarLong(long l)
    {
        throw unsupport;
    }
    
    @Override
    public long readVarLong()
    {
        throw unsupport;
    }
    
    @Override
    public ByteBuf<Void> writeVarChar(char c)
    {
        throw unsupport;
    }
    
    @Override
    public char readVarChar()
    {
        throw unsupport;
    }
    
    @Override
    public ByteBuf<Void> writeString(String value)
    {
        throw unsupport;
    }
    
    @Override
    public String readString()
    {
        throw unsupport;
    }
    
}
