package com.jfireframework.baseutil.collection;

/**
 * 自动增加的string缓存类，提供比stringbuilder更快的性能，和更好的接口
 * 
 * @author 林斌（windfire@zailanghua.com）
 * 
 */
public class StringCache
{
    private char[]      cache       = new char[512];
    private static char COMMA       = ',';
    private int         count       = 0;
    private int         cacheLength = cache.length;
    final static char[] DigitTens   = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
    };
    
    final static char[] DigitOnes   = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    };
    final static char[] digits      = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };
    
    /**
     * 使用字符串初始化对象，并且默认开启容量检查
     * 
     * @param str
     */
    public StringCache(String str)
    {
        clear();
        append(str);
    }
    
    /**
     * 初始化对象，并且默认开启容量检查
     */
    public StringCache()
    {
        clear();
    }
    
    /**
     * 清空缓存对象
     */
    public StringCache clear()
    {
        count = 0;
        return this;
    }
    
    public StringCache(int length)
    {
        cache = new char[length];
        cacheLength = length;
    }
    
    /**
     * 确认缓存char数组是否能容纳下length长度的字符，如果不能，则按照2倍长度进行数组扩充
     * 
     * @param length
     */
    private void ensureCapacity(int newCount)
    {
        if (cacheLength - newCount < 0)
        {
            cacheLength = (cacheLength + newCount) * 2;
            char[] newBuffer = new char[cacheLength];
            System.arraycopy(cache, 0, newBuffer, 0, count);
            cache = newBuffer;
        }
    }
    
    /**
     * 向字符串中追加一个str
     * 
     * @param str
     * @return
     */
    public StringCache append(String str)
    {
        if (str == null)
        {
            int newCount = count + 4;
            ensureCapacity(newCount);
            "null".getChars(0, 4, cache, count);
            count = newCount;
            return this;
        }
        int length = str.length();
        int newCount = count + length;
        ensureCapacity(newCount);
        str.getChars(0, length, cache, count);
        count = newCount;
        return this;
    }
    
    public StringCache append(StringCache cache)
    {
        char[] value = cache.cache;
        int length = cache.count;
        int newCount = length + count;
        ensureCapacity(newCount);
        System.arraycopy(value, 0, this.cache, count, length);
        count = newCount;
        return this;
    }
    
    /**
     * 向字符串追加一个char数组
     * 
     * @param value
     * @return
     */
    public StringCache append(char[] value)
    {
        int length = value.length;
        int newCount = length + count;
        ensureCapacity(count);
        System.arraycopy(value, 0, cache, count, length);
        count = newCount;
        return this;
    }
    
    /**
     * 向字符串追加一个char
     * 
     * @param c
     * @return
     */
    public StringCache append(char c)
    {
        int newCount = count + 1;
        ensureCapacity(newCount);
        cache[count] = c;
        count = newCount;
        return this;
    }
    
    /**
     * 向字符串追加一个char，并且检查剩余的容量是否满足length
     * 
     * @param c
     * @param length
     * @return
     */
    public StringCache append(char c, int length)
    {
        int newCount = count + length;
        ensureCapacity(newCount);
        cache[count] = c;
        count = newCount;
        return this;
    }
    
    /**
     * 向字符串追加一个数字
     * 
     * @param i
     * @return
     */
    public StringCache append(int i)
    {
        if (i == Integer.MIN_VALUE)
        {
            append("-2147483648");
            return this;
        }
        int length = (i < 0) ? stringSizeOf(-i) + 1 : stringSizeOf(i);
        int newCount = count + length;
        ensureCapacity(newCount);
        getChars(i, newCount, cache);
        count = newCount;
        return this;
    }
    
    private void getChars(int i, int index, char[] buf)
    {
        int q, r;
        int charPos = index;
        char sign = 0;
        if (i < 0)
        {
            sign = '-';
            i = -i;
        }
        while (i >= 65536)
        {
            q = i / 100;
            r = i - ((q << 6) + (q << 5) + (q << 2));
            i = q;
            buf[--charPos] = DigitOnes[r];
            buf[--charPos] = DigitTens[r];
        }
        for (;;)
        {
            q = (i * 52429) >>> (16 + 3);
            r = i - ((q << 3) + (q << 1)); // r = i-(q*10) ...
            buf[--charPos] = digits[r];
            i = q;
            if (i == 0)
                break;
        }
        if (sign != 0)
        {
            buf[--charPos] = sign;
        }
    }
    
    /**
     * 向字符串追加一个long型数字
     * 
     * @param l
     * @return
     */
    public StringCache append(long l)
    {
        if (l == Long.MIN_VALUE)
        {
            append("-9223372036854775808");
            return this;
        }
        int length = (l < 0) ? stringSizeOf(-l) + 1 : stringSizeOf(l);
        int newCount = count + length;
        ensureCapacity(newCount);
        getChars(l, newCount, cache);
        count = newCount;
        return this;
    }
    
    /**
     * 来自jdk的算法，看到不太明白，作用就是将long数字变成char数组，并且填入buf中
     * 
     * @param i
     * @param count
     * @param buf
     */
    private void getChars(long i, int count, char[] buf)
    {
        long q;
        int r;
        int charPos = count;
        char sign = 0;
        
        if (i < 0)
        {
            sign = '-';
            i = -i;
        }
        while (i > Integer.MAX_VALUE)
        {
            q = i / 100;
            r = (int) (i - ((q << 6) + (q << 5) + (q << 2)));
            i = q;
            buf[--charPos] = DigitOnes[r];
            buf[--charPos] = DigitTens[r];
        }
        int q2;
        int i2 = (int) i;
        while (i2 >= 65536)
        {
            q2 = i2 / 100;
            r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
            i2 = q2;
            buf[--charPos] = DigitOnes[r];
            buf[--charPos] = DigitTens[r];
        }
        for (;;)
        {
            q2 = (i2 * 52429) >>> (16 + 3);
            r = i2 - ((q2 << 3) + (q2 << 1)); // r = i2-(q2*10) ...
            buf[--charPos] = digits[r];
            i2 = q2;
            if (i2 == 0)
                break;
        }
        if (sign != 0)
        {
            buf[--charPos] = sign;
        }
    }
    
    /**
     * 向字符串追加一个float数字
     * 
     * @param f
     * @return
     */
    public StringCache append(float f)
    {
        return append(String.valueOf(f));
    }
    
    /**
     * 向字符串追加一个double数字
     * 
     * @param d
     * @return
     */
    public StringCache append(double d)
    {
        return append(String.valueOf(d));
    }
    
    /**
     * 向字符串追加一个bool值
     * 
     * @param value
     * @return
     */
    public StringCache append(boolean value)
    {
        if (value)
        {
            int newCount = count + 4;
            ensureCapacity(newCount);
            cache[count++] = 't';
            cache[count++] = 'r';
            cache[count++] = 'u';
            cache[count++] = 'e';
            count = newCount;
        }
        else
        {
            int newCount = count + 5;
            ensureCapacity(newCount);
            cache[count++] = 'f';
            cache[count++] = 'a';
            cache[count++] = 'l';
            cache[count++] = 's';
            cache[count++] = 'e';
            count = newCount;
        }
        return this;
    }
    
    public StringCache append(Integer integer)
    {
        return append(integer.intValue());
    }
    
    /**
     * 向字符串追加一个对象的string形式
     */
    public StringCache append(Object value)
    {
        if (value == null)
        {
            return append("null");
        }
        else
        {
            return append(value.toString());
        }
    }
    
    /**
     * 增加一个逗号
     * 
     * @return
     */
    public StringCache appendComma()
    {
        int newCount = count + 1;
        ensureCapacity(newCount);
        cache[count] = COMMA;
        count = newCount;
        return this;
    }
    
    /**
     * 删除最后一个字符
     * 
     * @return
     */
    public StringCache deleteLast()
    {
        count--;
        return this;
    }
    
    /**
     * 删除末尾指定个数的字符
     * 
     * @param num
     * @return
     */
    public StringCache deleteEnds(int num)
    {
        count -= num;
        return this;
    }
    
    /**
     * 确认最后一个值是否是逗号
     * 
     * @return
     */
    public boolean isCommaLast()
    {
        if (count == 0)
        {
            return false;
        }
        return cache[count - 1] == COMMA;
    }
    
    /**
     * 返回该缓存对象所对应的字符串
     */
    public String toString()
    {
        return new String(cache, 0, count);
    }
    
    private static final int[] intSize = new int[] { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };
    
    private static int stringSizeOf(int value)
    {
        for (int i = 0; i < 10; i++)
        {
            if (value <= intSize[i])
            {
                return i + 1;
            }
        }
        return 10;
    }
    
    private static int stringSizeOf(long value)
    {
        long p = 10;
        for (int i = 1; i < 19; i++)
        {
            if (value < p)
                return i;
            p = 10 * p;
        }
        return 19;
    }
    
    /**
     * 在字符串中放入str数组，以逗号间隔。
     * 
     * @param strs
     * @return
     */
    public StringCache appendStrsByComma(String... strs)
    {
        if (strs.length == 0)
        {
            return this;
        }
        for (String str : strs)
        {
            append(str).appendComma();
        }
        deleteLast();
        return this;
    }
    
    /**
     * 在字符串中放入num个str，以逗号区隔
     * 
     * @param str
     * @param num
     * @return
     */
    public StringCache appendStrsByComma(String str, int num)
    {
        if (num == 0)
        {
            return this;
        }
        for (int i = 0; i < num; i++)
        {
            append(str).appendComma();
        }
        deleteLast();
        return this;
    }
    
    /**
     * 将一个char数组加入到stringcache中
     * 
     * @param array char数组
     * @param off char数组中的偏移量
     * @param length 增加的长度
     * @return
     */
    public StringCache appendCharArray(char[] array, int off, int length)
    {
        int newCount = length + count;
        ensureCapacity(newCount);
        System.arraycopy(array, off, cache, count, length);
        count = newCount;
        return this;
    }
    
    /**
     * 返回内部使用的char数组
     * 
     * @return
     */
    public char[] getDirectArray()
    {
        return cache;
    }
    
    /**
     * 返回当前的内容长度
     * 
     * @return
     */
    public int count()
    {
        return count;
    }
    
    /**
     * 返回该str在cache中的匹配位置。不匹配则返回-1
     * 
     * @param str
     * @return
     */
    public int indexOf(String str)
    {
        if (str.length() > count)
        {
            return -1;
        }
        char[] target = str.toCharArray();
        int[] next = detailWithTarget(target);
        int length = target.length;
        for (int i = 0; i < count;)
        {
            int k = i, j = 0;
            for (; j < length;)
            {
                if (cache[k] == target[j])
                {
                    k++;
                    j++;
                }
                else
                {
                    i += next[j];
                    break;
                }
            }
            if (j == length)
            {
                return i;
            }
        }
        return -1;
    }
    
    private int[] detailWithTarget(char[] target)
    {
        int[] next = new int[target.length];
        next[0] = 1;
        for (int i = 1; i < target.length; i++)
        {
            next[i] = 1;
            for (int j = 1; j <= i; j++)
            {
                int j1 = j;
                for (int k = 0; j1 <= i; j1++, k++)
                {
                    if (target[k] == target[j1])
                    {
                        ;
                    }
                    else
                    {
                        break;
                    }
                }
                if (j1 == i)
                {
                    next[i] = j;
                    break;
                }
            }
        }
        return next;
    }
    
    /**
     * 从from位置截取内容并且以string的方式返回
     * 
     * @param from
     * @return
     */
    public String substring(int from)
    {
        return new String(cache, from, count - from);
    }
    
    public static void main(String[] args)
    {
        StringCache cache = new StringCache("find aba and abcabd and abcabe and abcabf");
        System.out.println(cache.substring(cache.indexOf("abcabd")));
    }
}
