package com.jfireframework.baseutil.uniqueid;

public interface Uid
{
    // 该数字代表2016-01-01所具备的毫秒数，以该毫秒数作为基准
    public final long base       = 1451577660000l;
    public final long short_mask = 0x000000000000003f;
    
    public byte[] generateBytes();
    
    public String generate();
    
    public String generateDigits();
}
