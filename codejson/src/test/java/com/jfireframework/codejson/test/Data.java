package com.jfireframework.codejson.test;

import java.util.ArrayList;
import java.util.HashMap;

public class Data
{
    private int                     a;
    private float                   b;
    private double                  c;
    private long                    d;
    private char                    e;
    private boolean                 f;
    private short                   g;
    private byte                    h;
    private Integer                 a1;
    private Float                   b1;
    private Double                  c1;
    private Long                    d1;
    private String                  e1;
    private Boolean                 f1;
    private Short                   g1;
    private Byte                    h1;
    private NestData                nestData;
    private ArrayList<String>       list;
    private ArrayList<NestData>     datas;
    private ArrayList<String>       nolist;
    private HashMap<String, String> map;
    private int[][]                 array2;
    private String[]                strs;
    private int[]                   array1;
    private char[]                  chars;
    private Integer[]               array3;
    private Integer[][]             array4;
    private NestData[]              nestDatas;
    // private HashMap<Date, NestData> map2;
    private ArrayList<String>[]     lists;
    private Object                  data;
    
    public boolean equal(Object target)
    {
        if (target instanceof Data)
        {
            Data entity = (Data) target;
            if (a != entity.getA() || b != entity.getB() || c != entity.getC() || d != entity.getD() || e != entity.getE() || f != entity.isF() || g != entity.getG() || h != entity.getH())
            {
                return false;
            }
            if (a1.equals(entity.getA1()) == false || b1.equals(entity.getB1()) == false || c1.equals(entity.getC1()) == false || d1.equals(entity.getD1()) == false || e1.equals(entity.getE1()) == false || f1.equals(entity.getF1()) == false || g1.equals(entity.getG1()) == false || h1.equals(entity.getH1()) == false)
            {
                return false;
            }
            if (nestData.equals(entity.getNestData()) == false)
            {
                return false;
            }
            if (list.equals(entity.getList()) == false)
            {
                return false;
            }
            if (datas.equals(entity.getDatas()) == false)
            {
                return false;
            }
            if (entity.getNolist().size() != 0)
            {
                return false;
            }
            if (map.equals(entity.getMap()) == false)
            {
                return false;
            }
            for (int i = 0; i < array2.length; i++)
            {
                for (int j = 0; j < array2[i].length; j++)
                {
                    if (array2[i][j] != entity.getArray2()[i][j])
                    {
                        return false;
                    }
                }
            }
            for (int i = 0; i < strs.length; i++)
            {
                if (strs[i].equals(entity.getStrs()[i]) == false)
                {
                    return false;
                }
            }
            for (int i = 0; i < array1.length; i++)
            {
                if (array1[i] != entity.getArray1()[i])
                {
                    return false;
                }
            }
            for (int i = 0; i < chars.length; i++)
            {
                if (chars[i] != entity.getChars()[i])
                {
                    return false;
                }
            }
            for (int i = 0; i < array3.length; i++)
            {
                if (array3[i].equals(entity.getArray3()[i]) == false)
                {
                    return false;
                }
            }
            for (int i = 0; i < array4.length; i++)
            {
                for (int j = 0; j < array4[i].length; j++)
                {
                    if (array4[i][j].equals(entity.getArray4()[i][j]) == false)
                    {
                        return false;
                    }
                }
            }
            // if (map2.equals(entity.getMap2()) == false)
            // {
            // return false;
            // }
            for (int i = 0; i < nestDatas.length; i++)
            {
                if (nestDatas[i].equals(entity.getNestDatas()[i]) == false)
                {
                    return false;
                }
            }
            for (int i = 0; i < lists.length; i++)
            {
                if (lists[i].equals(entity.getLists()[i]) == false)
                {
                    return false;
                }
            }
            if (entity.getData() == null)
            {
                return false;
            }
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public Object getData()
    {
        return data;
    }
    
    public void setData(Object data)
    {
        this.data = data;
    }
    
    public ArrayList<String>[] getLists()
    {
        return lists;
    }
    
    public void setLists(ArrayList<String>[] lists)
    {
        this.lists = lists;
    }
    
    public NestData[] getNestDatas()
    {
        return nestDatas;
    }
    
    public void setNestDatas(NestData[] nestDatas)
    {
        this.nestDatas = nestDatas;
    }
    
    // public HashMap<Date, NestData> getMap2()
    // {
    // return map2;
    // }
    //
    // public void setMap2(HashMap<Date, NestData> map2)
    // {
    // this.map2 = map2;
    // }
    
    public Integer[] getArray3()
    {
        return array3;
    }
    
    public void setArray3(Integer[] array3)
    {
        this.array3 = array3;
    }
    
    public Integer[][] getArray4()
    {
        return array4;
    }
    
    public void setArray4(Integer[][] array4)
    {
        this.array4 = array4;
    }
    
    public char[] getChars()
    {
        return chars;
    }
    
    public void setChars(char[] chars)
    {
        this.chars = chars;
    }
    
    public int[] getArray1()
    {
        return array1;
    }
    
    public void setArray1(int[] array1)
    {
        this.array1 = array1;
    }
    
    public String[] getStrs()
    {
        return strs;
    }
    
    public void setStrs(String[] strs)
    {
        this.strs = strs;
    }
    
    public int[][] getArray2()
    {
        return array2;
    }
    
    public void setArray2(int[][] array2)
    {
        this.array2 = array2;
    }
    
    public HashMap<String, String> getMap()
    {
        return map;
    }
    
    public void setMap(HashMap<String, String> map)
    {
        this.map = map;
    }
    
    public ArrayList<String> getNolist()
    {
        return nolist;
    }
    
    public void setNolist(ArrayList<String> nolist)
    {
        this.nolist = nolist;
    }
    
    public ArrayList<NestData> getDatas()
    {
        return datas;
    }
    
    public void setDatas(ArrayList<NestData> datas)
    {
        this.datas = datas;
    }
    
    public ArrayList<String> getList()
    {
        return list;
    }
    
    public void setList(ArrayList<String> list)
    {
        this.list = list;
    }
    
    public NestData getNestData()
    {
        return nestData;
    }
    
    public void setNestData(NestData nestData)
    {
        this.nestData = nestData;
    }
    
    public boolean isF()
    {
        return f;
    }
    
    public void setF(boolean f)
    {
        this.f = f;
    }
    
    public short getG()
    {
        return g;
    }
    
    public void setG(short g)
    {
        this.g = g;
    }
    
    public byte getH()
    {
        return h;
    }
    
    public void setH(byte h)
    {
        this.h = h;
    }
    
    public Boolean getF1()
    {
        return f1;
    }
    
    public void setF1(Boolean f1)
    {
        this.f1 = f1;
    }
    
    public Short getG1()
    {
        return g1;
    }
    
    public void setG1(Short g1)
    {
        this.g1 = g1;
    }
    
    public Byte getH1()
    {
        return h1;
    }
    
    public void setH1(Byte h1)
    {
        this.h1 = h1;
    }
    
    public int getA()
    {
        return a;
    }
    
    public void setA(int a)
    {
        this.a = a;
    }
    
    public float getB()
    {
        return b;
    }
    
    public void setB(float b)
    {
        this.b = b;
    }
    
    public double getC()
    {
        return c;
    }
    
    public void setC(double c)
    {
        this.c = c;
    }
    
    public long getD()
    {
        return d;
    }
    
    public void setD(long d)
    {
        this.d = d;
    }
    
    public char getE()
    {
        return e;
    }
    
    public void setE(char e)
    {
        this.e = e;
    }
    
    public Integer getA1()
    {
        return a1;
    }
    
    public void setA1(Integer a1)
    {
        this.a1 = a1;
    }
    
    public Float getB1()
    {
        return b1;
    }
    
    public void setB1(Float b1)
    {
        this.b1 = b1;
    }
    
    public Double getC1()
    {
        return c1;
    }
    
    public void setC1(Double c1)
    {
        this.c1 = c1;
    }
    
    public Long getD1()
    {
        return d1;
    }
    
    public void setD1(Long d1)
    {
        this.d1 = d1;
    }
    
    public String getE1()
    {
        return e1;
    }
    
    public void setE1(String e1)
    {
        this.e1 = e1;
    }
    
}
