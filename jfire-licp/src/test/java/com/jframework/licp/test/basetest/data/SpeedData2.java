package com.jframework.licp.test.basetest.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpeedData2
{
    // private Integer a = 213212312;
    // private Boolean b = true;
    // private Character c = 'd';
    // private Byte d = 0x11;
    // private Short e = 24;
    // private Long f = 1213124131312321L;
    // private Double g = 231321.2132;
    // private Float h = (float) 4986.2;
    // private Date i = new Date(466846979467694l);
    // private Integer[] j = new Integer[] { 1, 2, 4, 5 };
    // private Boolean[] k = new Boolean[] { true, false, true, false, false,
    // false, true };
    // private Character[] l = new Character[] { 'a', 'v', 'q', 'j', 'h', 'e',
    // 'f' };
    // private Byte[] m = new Byte[] { 0x32, 0x12, 0x34, (byte) 0x96 };
    // private Short[] n = new Short[] { 3, 8, 213, 451, 312, 45 };
    // private Long[] o = new Long[] { 12313131313l, 524141431313l,
    // 3131231231425l, 1313123121l };
    // private Double[] p = new Double[] { 6468613646.48646d, 4646.456d,
    // 546864648867.466d };
    // private Float[] q = new Float[] { 46486.2f, 49849.2f, 646854.6f };
    // private Date[] r = new Date[] { new Date(4646876464684l), new
    // Date(231323123121l) };
    // private int[][] w = new int[][] { { 1, 2 }, { 3, 4, 5 } };
    private List<SpeedData>             list = new ArrayList<SpeedData>();
    private HashMap<Integer, SpeedData> map  = new HashMap<Integer, SpeedData>();
    // private SpeedData speedData = new SpeedData();
    
    public SpeedData2()
    {
        for (int i = 0; i < 20; i++)
        {
            SpeedData baseData = new SpeedData();
            list.add(baseData);
//            map.put(i, baseData);
        }
    }
}
