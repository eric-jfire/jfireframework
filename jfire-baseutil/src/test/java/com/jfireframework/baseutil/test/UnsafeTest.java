package com.jfireframework.baseutil.test;

import java.lang.reflect.Array;
import java.util.Arrays;
import org.junit.Test;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeTest
{
    private Unsafe unsafe = ReflectUtil.getUnsafe();
    
    @Test
    public void test()
    {
        int[] dim = new int[2];
        dim[0] = 2;
        int[][] a = (int[][]) Array.newInstance(int.class, dim);
        a[1] = new int[] { 1, 2, 3, 43, 54, 5, 6, 6, 7 };
        System.out.println(a[1][6]);
    }
}
