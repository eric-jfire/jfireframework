package com.jfireframework.fose.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LongData
{
	private int                        a    = 213212312;
	private boolean                    b    = false;
	private char                       c    = 'c';
	private byte                       d    = 0x11;
	private short                      e    = 24;
	private long                       f    = 1213124131312321L;
	private double                     g    = 231321.2132;
	private float                      h    = (float) 4986.2;
	private String                     i    = "dzzz";
	private int[]                      j    = new int[] { 1, 2, 4, 5 };
	private boolean[]                  k    = new boolean[] { true, false, true, false, false, false, true };
	private char[]                     l    = new char[] { 'a', 'v', 'q', 'j', 'h', 'e', 'f' };
	private byte[]                     m    = new byte[] { 0x32, 0x12, 0x34, (byte) 0x96 };
	private short[]                    n    = new short[] { 3, 8, 213, 451, 312, 45 };
	private long[]                     o    = new long[] { 12313131313l, 524141431313l, 3131231231425l, 1313123121l };
	private double[]                   p    = new double[] { 6468613646.48646d, 4646.456d, 546864648867.466d };
	private float[]                    q    = new float[] { 46486.2f, 49849.2f, 646854.6f };
	private String[]                   r    = new String[] { "adasdccczzzzasdasd", "dsadqzzzzzzz2eafsa" };
	private Integer                    a1   = 213212312;
	private Boolean                    b1   = false;
	private Character                  c1   = 'd';
	private Byte                       d1   = 0x11;
	private Short                      e1   = 24;
	private Long                       f1   = 1213124131312321L;
	private Double                     g1   = 231321.2132;
	private Float                      h1   = (float) 4986.2;
	private Integer[]                  j1   = new Integer[] { 1, 2, 4, 5 };
	private Boolean[]                  k1   = new Boolean[] { true, false, true, false, false, false, true };
	private Character[]                l1   = new Character[] { 'a', 'v', 'q', 'j', 'h', 'e', 'f' };
	private Byte[]                     m1   = new Byte[] { 0x32, 0x12, 0x34, (byte) 0x96 };
	private Short[]                    n1   = new Short[] { 3, 8, 213, 451, 312, 45 };
	private Long[]                     o1   = new Long[] { 12313131313l, 524141431313l,
	                                        3131231231425l, 1313123121l };
	private Double[]                   p1   = new Double[] { 6468613646.48646d, 4646.456d,
	                                        546864648867.466d };
	private Float[]                    q1   = new Float[] { 46486.2f, 49849.2f, 646854.6f };
	private int[][]                    w    = new int[][] { { 1, 2 }, { 3, 4, 5 } };
	private List<BaseData>             list = new ArrayList<>();
	private HashMap<Integer, BaseData> map  = new HashMap<>();
	private Object                     j2   = new int[] { 1, 2, 4, 5 };
	private Object                     k2   = new boolean[] { true, false, true, false, false, false, true };
	private Object                     l2   = new char[] { 'a', 'v', 'q', 'j', 'h', 'e', 'f' };
	private Object                     m2   = new byte[] { 0x32, 0x12, 0x34, (byte) 0x96 };
	private Object                     n2   = new short[] { 3, 8, 213, 451, 312, 45 };
	private Object                     o2   = new long[] { 12313131313l, 524141431313l, 3131231231425l, 1313123121l };
	private Object                     p2   = new double[] { 6468613646.48646d, 4646.456d, 546864648867.466d };
	private Object                     q2   = new float[] { 46486.2f, 49849.2f, 646854.6f };
	private Object                     r2   = new String[] { "adasdasccccccccccccccdasd", "dsadq2eccccccccafsa" };
	private Object                     j3   = new Integer[] { 1, 2, 4, 5 };
	private Object                     k3   = new Boolean[] { true, false, true, false, false, false, true };
	private Object                     l3   = new Character[] { 'a', 'v', 'q', 'j', 'h', 'e', 'f' };
	private Object                     m3   = new Byte[] { 0x32, 0x12, 0x34, (byte) 0x96 };
	private Object                     n3   = new Short[] { 3, 8, 213, 451, 312, 45 };
	private Object                     o3   = new Long[] { 12313131313l, 524141431313l,
	                                        3131231231425l, 1313123121l };
	private Double[]                   p3   = new Double[] { 6468613646.48646d, 4646.456d,
	                                        546864648867.466d };
	private Float[]                    q3   = new Float[] { 46486.2f, 49849.2f, 646854.6f };
	private int[][]                    w3   = new int[][] { { 1, 2 }, { 3, 4, 5 } };
	
	// private BaseData baseData = new BaseData();
	public LongData()
	{
		for (int i = 0; i < 4; i++)
		{
			list.add(new BaseData(i));
			map.put(i, new BaseData(i + 11));
		}
	}
	
	// public Object getJ2()
	// {
	// return j2;
	// }
	//
	// public void setJ2(Object j2)
	// {
	// this.j2 = j2;
	// }
	//
	// public Object getK2()
	// {
	// return k2;
	// }
	//
	// public void setK2(Object k2)
	// {
	// this.k2 = k2;
	// }
	//
	// public Object getL2()
	// {
	// return l2;
	// }
	//
	// public void setL2(Object l2)
	// {
	// this.l2 = l2;
	// }
	//
	// public Object getM2()
	// {
	// return m2;
	// }
	//
	// public void setM2(Object m2)
	// {
	// this.m2 = m2;
	// }
	//
	// public Object getN2()
	// {
	// return n2;
	// }
	//
	// public void setN2(Object n2)
	// {
	// this.n2 = n2;
	// }
	//
	// public Object getO2()
	// {
	// return o2;
	// }
	//
	// public void setO2(Object o2)
	// {
	// this.o2 = o2;
	// }
	//
	// public Object getP2()
	// {
	// return p2;
	// }
	//
	// public void setP2(Object p2)
	// {
	// this.p2 = p2;
	// }
	//
	// public Object getQ2()
	// {
	// return q2;
	// }
	//
	// public void setQ2(Object q2)
	// {
	// this.q2 = q2;
	// }
	//
	// public Object getR2()
	// {
	// return r2;
	// }
	//
	// public void setR2(Object r2)
	// {
	// this.r2 = r2;
	// }
	//
	// public Object getJ3()
	// {
	// return j3;
	// }
	//
	// public void setJ3(Object j3)
	// {
	// this.j3 = j3;
	// }
	//
	// public Object getK3()
	// {
	// return k3;
	// }
	//
	// public void setK3(Object k3)
	// {
	// this.k3 = k3;
	// }
	//
	// public Object getL3()
	// {
	// return l3;
	// }
	//
	// public void setL3(Object l3)
	// {
	// this.l3 = l3;
	// }
	//
	// public Object getM3()
	// {
	// return m3;
	// }
	//
	// public void setM3(Object m3)
	// {
	// this.m3 = m3;
	// }
	//
	// public Object getN3()
	// {
	// return n3;
	// }
	//
	// public void setN3(Object n3)
	// {
	// this.n3 = n3;
	// }
	//
	// public Object getO3()
	// {
	// return o3;
	// }
	//
	// public void setO3(Object o3)
	// {
	// this.o3 = o3;
	// }
	//
	// public Double[] getP3()
	// {
	// return p3;
	// }
	//
	// public void setP3(Double[] p3)
	// {
	// this.p3 = p3;
	// }
	//
	// public Float[] getQ3()
	// {
	// return q3;
	// }
	//
	// public void setQ3(Float[] q3)
	// {
	// this.q3 = q3;
	// }
	//
	// public int[][] getW3()
	// {
	// return w3;
	// }
	//
	// public void setW3(int[][] w3)
	// {
	// this.w3 = w3;
	// }
	//
	// public int getA()
	// {
	// return a;
	// }
	//
	// public void setA(int a)
	// {
	// this.a = a;
	// }
	//
	// public boolean isB()
	// {
	// return b;
	// }
	//
	// public void setB(boolean b)
	// {
	// this.b = b;
	// }
	//
	// public char getC()
	// {
	// return c;
	// }
	//
	// public void setC(char c)
	// {
	// this.c = c;
	// }
	//
	// public byte getD()
	// {
	// return d;
	// }
	//
	// public void setD(byte d)
	// {
	// this.d = d;
	// }
	//
	// public short getE()
	// {
	// return e;
	// }
	//
	// public void setE(short e)
	// {
	// this.e = e;
	// }
	//
	// public long getF()
	// {
	// return f;
	// }
	//
	// public void setF(long f)
	// {
	// this.f = f;
	// }
	//
	// public double getG()
	// {
	// return g;
	// }
	//
	// public void setG(double g)
	// {
	// this.g = g;
	// }
	//
	// public float getH()
	// {
	// return h;
	// }
	//
	// public void setH(float h)
	// {
	// this.h = h;
	// }
	//
	// public String getI()
	// {
	// return i;
	// }
	//
	// public void setI(String i)
	// {
	// this.i = i;
	// }
	//
	// public int[] getJ()
	// {
	// return j;
	// }
	//
	// public void setJ(int[] j)
	// {
	// this.j = j;
	// }
	//
	// public boolean[] getK()
	// {
	// return k;
	// }
	//
	// public void setK(boolean[] k)
	// {
	// this.k = k;
	// }
	//
	// public char[] getL()
	// {
	// return l;
	// }
	//
	// public void setL(char[] l)
	// {
	// this.l = l;
	// }
	//
	// public byte[] getM()
	// {
	// return m;
	// }
	//
	// public void setM(byte[] m)
	// {
	// this.m = m;
	// }
	//
	// public short[] getN()
	// {
	// return n;
	// }
	//
	// public void setN(short[] n)
	// {
	// this.n = n;
	// }
	//
	// public long[] getO()
	// {
	// return o;
	// }
	//
	// public void setO(long[] o)
	// {
	// this.o = o;
	// }
	//
	// public double[] getP()
	// {
	// return p;
	// }
	//
	// public void setP(double[] p)
	// {
	// this.p = p;
	// }
	//
	// public float[] getQ()
	// {
	// return q;
	// }
	//
	// public void setQ(float[] q)
	// {
	// this.q = q;
	// }
	//
	// public String[] getR()
	// {
	// return r;
	// }
	//
	// public void setR(String[] r)
	// {
	// this.r = r;
	// }
	//
	// public Integer getA1()
	// {
	// return a1;
	// }
	//
	// public void setA1(Integer a1)
	// {
	// this.a1 = a1;
	// }
	//
	// public Boolean getB1()
	// {
	// return b1;
	// }
	//
	// public void setB1(Boolean b1)
	// {
	// this.b1 = b1;
	// }
	//
	// public Character getC1()
	// {
	// return c1;
	// }
	//
	// public void setC1(Character c1)
	// {
	// this.c1 = c1;
	// }
	//
	// public Byte getD1()
	// {
	// return d1;
	// }
	//
	// public void setD1(Byte d1)
	// {
	// this.d1 = d1;
	// }
	//
	// public Short getE1()
	// {
	// return e1;
	// }
	//
	// public void setE1(Short e1)
	// {
	// this.e1 = e1;
	// }
	//
	// public Long getF1()
	// {
	// return f1;
	// }
	//
	// public void setF1(Long f1)
	// {
	// this.f1 = f1;
	// }
	//
	// public Double getG1()
	// {
	// return g1;
	// }
	//
	// public void setG1(Double g1)
	// {
	// this.g1 = g1;
	// }
	//
	// public Float getH1()
	// {
	// return h1;
	// }
	//
	// public void setH1(Float h1)
	// {
	// this.h1 = h1;
	// }
	//
	// public Integer[] getJ1()
	// {
	// return j1;
	// }
	//
	// public void setJ1(Integer[] j1)
	// {
	// this.j1 = j1;
	// }
	//
	// public Boolean[] getK1()
	// {
	// return k1;
	// }
	//
	// public void setK1(Boolean[] k1)
	// {
	// this.k1 = k1;
	// }
	//
	// public Character[] getL1()
	// {
	// return l1;
	// }
	//
	// public void setL1(Character[] l1)
	// {
	// this.l1 = l1;
	// }
	//
	// public Byte[] getM1()
	// {
	// return m1;
	// }
	//
	// public void setM1(Byte[] m1)
	// {
	// this.m1 = m1;
	// }
	//
	// public Short[] getN1()
	// {
	// return n1;
	// }
	//
	// public void setN1(Short[] n1)
	// {
	// this.n1 = n1;
	// }
	//
	// public Long[] getO1()
	// {
	// return o1;
	// }
	//
	// public void setO1(Long[] o1)
	// {
	// this.o1 = o1;
	// }
	//
	// public Double[] getP1()
	// {
	// return p1;
	// }
	//
	// public void setP1(Double[] p1)
	// {
	// this.p1 = p1;
	// }
	//
	// public Float[] getQ1()
	// {
	// return q1;
	// }
	//
	// public void setQ1(Float[] q1)
	// {
	// this.q1 = q1;
	// }
	//
	// public int[][] getW()
	// {
	// return w;
	// }
	//
	// public void setW(int[][] w)
	// {
	// this.w = w;
	// }
	//
	// public List<BaseData> getList()
	// {
	// return list;
	// }
	//
	// public void setList(List<BaseData> list)
	// {
	// this.list = list;
	// }
	//
	// public HashMap<Integer, BaseData> getMap()
	// {
	// return map;
	// }
	//
	// public void setMap(HashMap<Integer, BaseData> map)
	// {
	// this.map = map;
	// }
	//
	
}
