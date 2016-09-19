package com.jfireframework.baseutil.concurrent;

public interface EnsureMultithreadedVisibility
{
    /**
     * 保证这个对象的属性数据在多线程中的可见性，借由Hb关系来保证。
     * 实现思路：
     * 使用一个无关的volatile 变量。在多线程环境中，第一个要读取数据之前，先读取这个volatile无关变量.
     * 这样，后续的数据读取就保证可以读取到最新的数据。
     * 也就是其他的属性无需用volatile修饰
     * 这里需要两个方法配合。
     * 一个写，一个读。写方法在所有普通属性被设置后调用，对volatile修饰的无关属性写入一个值。
     * 读方法在读取所有属性前调用，读取volatile修饰的属性。
     */
    public void ensureMultithreadedVisibilityForRead();
    
    public void ensureMultithreadedVisibilityForWrite();
}
