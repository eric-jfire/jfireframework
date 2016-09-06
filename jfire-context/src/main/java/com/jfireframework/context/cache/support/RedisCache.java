package com.jfireframework.context.cache.support;

import javax.annotation.PostConstruct;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBuf;
import com.jfireframework.context.cache.Cache;
import com.jfireframework.licp.Licp;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisCache implements Cache
{
    private String            name;
    private String            ip;
    private int               port;
    private JedisPool         jedisPool;
    private ThreadLocal<Licp> licps = new ThreadLocal<Licp>() {
                                        @Override
                                        protected Licp initialValue()
                                        {
                                            return new Licp();
                                        }
                                    };
    
    @PostConstruct
    public void init()
    {
        jedisPool = new JedisPool(ip, port);
    }
    
    @Override
    public void put(Object key, Object value)
    {
        Jedis jedis = jedisPool.getResource();
        ByteBuf<?> buf = HeapByteBuf.allocate(100);
        try
        {
            Licp licp = licps.get();
            licp.serialize(key, buf);
            byte[] key_bytes = buf.toArray();
            licp.serialize(value, buf.clear());
            byte[] value_bytes = buf.toArray();
            jedis.set(key_bytes, value_bytes);
        }
        finally
        {
            buf.release();
            jedis.close();
        }
    }
    
    @Override
    public void put(Object key, Object value, int timeToLive)
    {
        Jedis jedis = jedisPool.getResource();
        ByteBuf<?> buf = HeapByteBuf.allocate(100);
        try
        {
            Licp licp = licps.get();
            licp.serialize(key, buf);
            byte[] key_bytes = buf.toArray();
            licp.serialize(value, buf.clear());
            byte[] value_bytes = buf.toArray();
            jedis.setex(key_bytes, timeToLive, value_bytes);
        }
        finally
        {
            buf.release();
            jedis.close();
        }
    }
    
    @Override
    public Object get(Object key)
    {
        Licp licp = licps.get();
        ByteBuf<?> buf = HeapByteBuf.allocate(100);
        Jedis jedis = jedisPool.getResource();
        try
        {
            licp.serialize(key, buf);
            byte[] key_bytes = buf.toArray();
            Object result = jedis.get(key_bytes);
            return result;
        }
        finally
        {
            buf.release();
            jedis.close();
        }
    }
    
    @Override
    public void remove(Object key)
    {
        Licp licp = licps.get();
        ByteBuf<?> buf = HeapByteBuf.allocate(100);
        Jedis jedis = jedisPool.getResource();
        try
        {
            licp.serialize(key, buf);
            byte[] key_bytes = buf.toArray();
            jedis.del(key_bytes);
        }
        finally
        {
            buf.release();
            jedis.close();
        }
    }
    
    @Override
    public void clear()
    {
        Jedis jedis = jedisPool.getResource();
        try
        {
            jedis.flushDB();
        }
        finally
        {
            jedis.close();
        }
    }
    
    @Override
    public String getName()
    {
        return name;
    }
    
}
