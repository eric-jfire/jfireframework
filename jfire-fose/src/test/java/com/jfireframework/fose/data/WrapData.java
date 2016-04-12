package com.jfireframework.fose.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrapData
{
	private Integer                    a    = 213212312;
	private Boolean                    b    = true;
	private Character                  c    = 'd';
	private Byte                       d    = 0x11;
	private Short                      e    = 24;
	private Long                       f    = 1213124131312321L;
	private Double                     g    = 231321.2132;
	private Float                      h    = (float) 4986.2;
	private Date                       i    = new Date(466846979467694l);
	private Integer[]                  j    = new Integer[] { 1, 2, 4, 5 };
	private Boolean[]                  k    = new Boolean[] { true, false, true, false, false, false, true };
	private Character[]                l    = new Character[] { 'a', 'v', 'q', 'j', 'h', 'e', 'f' };
	private Byte[]                     m    = new Byte[] { 0x32, 0x12, 0x34, (byte) 0x96 };
	private Short[]                    n    = new Short[] { 3, 8, 213, 451, 312, 45 };
	private Long[]                     o    = new Long[] { 12313131313l, 524141431313l, 3131231231425l, 1313123121l };
	private Double[]                   p    = new Double[] { 6468613646.48646d, 4646.456d, 546864648867.466d };
	private Float[]                    q    = new Float[] { 46486.2f, 49849.2f, 646854.6f };
	private Date[]                     r    = new Date[] { new Date(4646876464684l), new Date(231323123121l) };
	private int[][]                    w    = new int[][] { { 1, 2 }, { 3, 4, 5 } };
	private List<BaseData>             list = new ArrayList<>();
	private HashMap<Integer, BaseData> map  = new HashMap<>();
	
	public WrapData()
	{
		for (int i = 0; i < 10; i++)
		{
			BaseData baseData = new BaseData(i);
			list.add(baseData);
			map.put(i, baseData);
		}
	}
	
	public Integer getA()
	{
		return a;
	}
	
	public void setA(Integer a)
	{
		this.a = a;
	}
	
	public Boolean getB()
	{
		return b;
	}
	
	public void setB(Boolean b)
	{
		this.b = b;
	}
	
	public Character getC()
	{
		return c;
	}
	
	public void setC(Character c)
	{
		this.c = c;
	}
	
	public Byte getD()
	{
		return d;
	}
	
	public void setD(Byte d)
	{
		this.d = d;
	}
	
	public Short getE()
	{
		return e;
	}
	
	public void setE(Short e)
	{
		this.e = e;
	}
	
	public Long getF()
	{
		return f;
	}
	
	public void setF(Long f)
	{
		this.f = f;
	}
	
	public Double getG()
	{
		return g;
	}
	
	public void setG(Double g)
	{
		this.g = g;
	}
	
	public Float getH()
	{
		return h;
	}
	
	public void setH(Float h)
	{
		this.h = h;
	}
	
	public Date getI()
	{
		return i;
	}
	
	public void setI(Date i)
	{
		this.i = i;
	}
	
	public Integer[] getJ()
	{
		return j;
	}
	
	public void setJ(Integer[] j)
	{
		this.j = j;
	}
	
	public Boolean[] getK()
	{
		return k;
	}
	
	public void setK(Boolean[] k)
	{
		this.k = k;
	}
	
	public Character[] getL()
	{
		return l;
	}
	
	public void setL(Character[] l)
	{
		this.l = l;
	}
	
	public Byte[] getM()
	{
		return m;
	}
	
	public void setM(Byte[] m)
	{
		this.m = m;
	}
	
	public Short[] getN()
	{
		return n;
	}
	
	public void setN(Short[] n)
	{
		this.n = n;
	}
	
	public Long[] getO()
	{
		return o;
	}
	
	public void setO(Long[] o)
	{
		this.o = o;
	}
	
	public Double[] getP()
	{
		return p;
	}
	
	public void setP(Double[] p)
	{
		this.p = p;
	}
	
	public Float[] getQ()
	{
		return q;
	}
	
	public void setQ(Float[] q)
	{
		this.q = q;
	}
	
	public Date[] getR()
	{
		return r;
	}
	
	public void setR(Date[] r)
	{
		this.r = r;
	}
	
	public int[][] getW()
	{
		return w;
	}
	
	public void setW(int[][] w)
	{
		this.w = w;
	}
	
	public List<BaseData> getList()
	{
		return list;
	}
	
	public void setList(List<BaseData> list)
	{
		this.list = list;
	}
	
	public void setMap(HashMap<Integer, BaseData> map)
	{
		this.map = map;
	}
	
	public Map<Integer, BaseData> getMap()
	{
		return map;
	}
	
}
