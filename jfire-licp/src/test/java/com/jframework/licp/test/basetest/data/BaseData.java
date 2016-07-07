package com.jframework.licp.test.basetest.data;

import java.util.Date;
import java.util.Random;
import com.jfireframework.baseutil.code.RandomString;

public class BaseData
{
    private Date[]      dates = new Date[] { new Date(13536156), new Date(54454655) };
    private int         index = 0;
    public int          a     = 213212312;
    private boolean     b     = false;
    private char        c     = 'c';
    private byte        d     = 0x11;
    private short       e     = 24;
    private long        f     = 1213124131312321L;
    private double      g     = 231321.2132;
    private float       h     = (float) 4986.2;
    private String      i     = "123452312316789a";
    private int[]       j     = new int[] { 1, 2, 4, 5 };
    private boolean[]   k     = new boolean[] { true, false, true, false, false, false, true };
    private char[]      l     = new char[] { 'a', 'v', 'q', 'j', 'h', 'e', 'f' };
    private byte[]      m     = new byte[] { 0x32, 0x12, 0x34, (byte) 0x96 };
    private short[]     n     = new short[] { 3, 8, 213, 451, 312, 45 };
    private long[]      o     = new long[] { 12313131313l, 524141431313l, 3131231231425l, 1313123121l };
    private double[]    p     = new double[] { 6468613646.48646d, 4646.456d, 546864648867.466d };
    private float[]     q     = new float[] { 46486.2f, 49849.2f, 646854.6f };
    private String[]    r     = new String[] { "abcdf12345", "abdfcgf12323" };
    private int[][]     j2    = new int[][] { { 1, 2, 4, 5 }, { 1, 2, 3, 4, 5, 6 }
    };
    private boolean[][] k2    = new boolean[][] { { true, false, true, false, false, false, true }, { true, false, true, false, false, false, false }
    };
    private char[][]    l2    = new char[][] { { 'a', 'v', 'q', 'j', 'h', 'e', 'f'
    }, { 'a', 'v', 'q', 'j', 'h', 'e', 'f' } };
    private byte[][]    m2    = new byte[][] { { 0x32, 0x12, 0x34, (byte) 0x96 }, { 0x32, 0x12, 0x34, (byte) 0x96 } };
    private short[][]   n2    = new short[][] { { 3, 8, 213, 451, 312, 45 }, { 3, 8, 213, 451, 312, 45 } };
    private long[][]    o2    = new long[][] { { 12313131313l, 524141431313l, 3131231231425l, 1313123121l }, { 12313131313l, 524141431313l, 3131231231425l, 1313123121l } };
    private double[][]  p2    = new double[][] { { 6468613646.48646d, 4646.456d, 546864648867.466d }, { 6468613646.48646d, 4646.456d, 546864648867.466d }
    };
    private float[][]   q2    = new float[][] { { 46486.2f, 49849.2f, 646854.6f }, { 46486.2f, 49849.2f, 646854.6f } };
    private String[][]  r2    = new String[2][];
    
    private Object      j1    = new int[] { 1, 2, 4, 5 };
    private Object      k1    = new boolean[] { true, false, true, false, false, false, true };
    private Object      l1    = new char[] { 'a', 'v', 'q', 'j', 'h', 'e', 'f' };
    private Object      m1    = new byte[] { 0x32, 0x12, 0x34, (byte) 0x96 };
    private Object      n1    = new short[] { 3, 8, 213, 451, 312, 45 };
    private Object      o1    = new long[] { 12313131313l, 524141431313l, 3131231231425l, 1313123121l };
    private Object      p1    = new double[] { 6468613646.48646d, 4646.456d, 546864648867.466d };
    private Object      q1    = new float[] { 46486.2f, 49849.2f, 646854.6f };
    private Object      r1    = new String[] { "adaseqeddasdasd", "dsadqeq2eafsa" };
    
    public BaseData()
    {
        
    }
    
    public boolean equals(Object target)
    {
        if (target instanceof BaseData)
        {
            BaseData baseData = this;
            BaseData result = (BaseData) target;
            if (baseData.getIndex() != result.getIndex())
            {
                return false;
            }
            if (result.getA() != baseData.getA())
            {
                return false;
            }
            if (result.isB() != baseData.isB())
            {
                return false;
            }
            if (result.getC() != baseData.getC())
            {
                return false;
            }
            if (result.getD() != baseData.getD())
            {
                return false;
            }
            if (result.getE() != baseData.getE())
            {
                return false;
            }
            if (result.getH() != baseData.getH())
            {
                return false;
            }
            if (result.getI().equals(baseData.getI()) == false)
            {
                return false;
            }
            for (int i = 0; i < result.getJ().length; i++)
            {
                if (result.getJ()[i] != baseData.getJ()[i])
                {
                    return false;
                }
            }
            for (int i = 0; i < result.getK().length; i++)
            {
                if (result.getK()[i] != baseData.getK()[i])
                {
                    return false;
                }
            }
            for (int i = 0; i < result.getL().length; i++)
            {
                if (result.getL()[i] != baseData.getL()[i])
                {
                    return false;
                }
            }
            for (int i = 0; i < result.getM().length; i++)
            {
                if (result.getM()[i] != baseData.getM()[i])
                {
                    return false;
                }
            }
            
            for (int i = 0; i < result.getN().length; i++)
            {
                if (result.getN()[i] != baseData.getN()[i])
                {
                    return false;
                }
            }
            for (int i = 0; i < result.getO().length; i++)
            {
                if (result.getO()[i] != baseData.getO()[i])
                {
                    return false;
                }
            }
            for (int i = 0; i < result.getP().length; i++)
            {
                if (result.getP()[i] != baseData.getP()[i])
                {
                    return false;
                }
            }
            for (int i = 0; i < result.getR().length; i++)
            {
                if (result.getR()[i].equals(baseData.getR()[i]) == false)
                {
                    return false;
                }
            }
            for (int i = 0; i < ((int[]) result.getJ1()).length; i++)
            {
                if (((int[]) result.getJ1())[i] != ((int[]) baseData.getJ1())[i])
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public BaseData(int index)
    {
        Random random = new Random();
        this.index = index;
        i = String.valueOf(System.nanoTime() + random.nextInt());
        r[0] = RandomString.randomString(20);
        r[1] = RandomString.randomString(20);
        ((String[]) r1)[0] = RandomString.randomString(20);
        ((String[]) r1)[1] = RandomString.randomString(20);
        r2[0] = new String[] { RandomString.randomString(20), RandomString.randomString(20) };
        r2[1] = new String[] { RandomString.randomString(20), RandomString.randomString(20) };
    }
    
    public short[] getN()
    {
        return n;
    }
    
    public void setN(short[] n)
    {
        this.n = n;
    }
    
    public int getIndex()
    {
        return index;
    }
    
    public void setIndex(int index)
    {
        this.index = index;
    }
    
    public int getA()
    {
        return a;
    }
    
    public void setA(int a)
    {
        this.a = a;
    }
    
    public boolean isB()
    {
        return b;
    }
    
    public void setB(boolean b)
    {
        this.b = b;
    }
    
    public char getC()
    {
        return c;
    }
    
    public void setC(char c)
    {
        this.c = c;
    }
    
    public byte getD()
    {
        return d;
    }
    
    public void setD(byte d)
    {
        this.d = d;
    }
    
    public short getE()
    {
        return e;
    }
    
    public void setE(short e)
    {
        this.e = e;
    }
    
    public long getF()
    {
        return f;
    }
    
    public void setF(long f)
    {
        this.f = f;
    }
    
    public double getG()
    {
        return g;
    }
    
    public void setG(double g)
    {
        this.g = g;
    }
    
    public float getH()
    {
        return h;
    }
    
    public void setH(float h)
    {
        this.h = h;
    }
    
    public String getI()
    {
        return i;
    }
    
    public void setI(String i)
    {
        this.i = i;
    }
    
    public int[] getJ()
    {
        return j;
    }
    
    public void setJ(int[] j)
    {
        this.j = j;
    }
    
    public boolean[] getK()
    {
        return k;
    }
    
    public void setK(boolean[] k)
    {
        this.k = k;
    }
    
    public char[] getL()
    {
        return l;
    }
    
    public void setL(char[] l)
    {
        this.l = l;
    }
    
    public byte[] getM()
    {
        return m;
    }
    
    public void setM(byte[] m)
    {
        this.m = m;
    }
    
    public long[] getO()
    {
        return o;
    }
    
    public void setO(long[] o)
    {
        this.o = o;
    }
    
    public double[] getP()
    {
        return p;
    }
    
    public void setP(double[] p)
    {
        this.p = p;
    }
    
    public float[] getQ()
    {
        return q;
    }
    
    public void setQ(float[] q)
    {
        this.q = q;
    }
    
    public String[] getR()
    {
        return r;
    }
    
    public void setR(String[] r)
    {
        this.r = r;
    }
    
    public int[][] getJ2()
    {
        return j2;
    }
    
    public void setJ2(int[][] j2)
    {
        this.j2 = j2;
    }
    
    public boolean[][] getK2()
    {
        return k2;
    }
    
    public void setK2(boolean[][] k2)
    {
        this.k2 = k2;
    }
    
    public char[][] getL2()
    {
        return l2;
    }
    
    public void setL2(char[][] l2)
    {
        this.l2 = l2;
    }
    
    public byte[][] getM2()
    {
        return m2;
    }
    
    public void setM2(byte[][] m2)
    {
        this.m2 = m2;
    }
    
    public short[][] getN2()
    {
        return n2;
    }
    
    public void setN2(short[][] n2)
    {
        this.n2 = n2;
    }
    
    public long[][] getO2()
    {
        return o2;
    }
    
    public void setO2(long[][] o2)
    {
        this.o2 = o2;
    }
    
    public double[][] getP2()
    {
        return p2;
    }
    
    public void setP2(double[][] p2)
    {
        this.p2 = p2;
    }
    
    public float[][] getQ2()
    {
        return q2;
    }
    
    public void setQ2(float[][] q2)
    {
        this.q2 = q2;
    }
    
    public String[][] getR2()
    {
        return r2;
    }
    
    public void setR2(String[][] r2)
    {
        this.r2 = r2;
    }
    
    public Object getJ1()
    {
        return j1;
    }
    
    public void setJ1(Object j1)
    {
        this.j1 = j1;
    }
    
    public Object getK1()
    {
        return k1;
    }
    
    public void setK1(Object k1)
    {
        this.k1 = k1;
    }
    
    public Object getL1()
    {
        return l1;
    }
    
    public void setL1(Object l1)
    {
        this.l1 = l1;
    }
    
    public Object getM1()
    {
        return m1;
    }
    
    public void setM1(Object m1)
    {
        this.m1 = m1;
    }
    
    public Object getN1()
    {
        return n1;
    }
    
    public void setN1(Object n1)
    {
        this.n1 = n1;
    }
    
    public Object getO1()
    {
        return o1;
    }
    
    public void setO1(Object o1)
    {
        this.o1 = o1;
    }
    
    public Object getP1()
    {
        return p1;
    }
    
    public void setP1(Object p1)
    {
        this.p1 = p1;
    }
    
    public Object getQ1()
    {
        return q1;
    }
    
    public void setQ1(Object q1)
    {
        this.q1 = q1;
    }
    
    public Object getR1()
    {
        return r1;
    }
    
    public void setR1(Object r1)
    {
        this.r1 = r1;
    }
    
    public Date[] getDates()
    {
        return dates;
    }
    
    public void setDates(Date[] dates)
    {
        this.dates = dates;
    }
    
}
