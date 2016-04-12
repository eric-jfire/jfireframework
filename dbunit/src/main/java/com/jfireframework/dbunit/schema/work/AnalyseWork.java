package com.jfireframework.dbunit.schema.work;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.dbunit.table.Table;

public class AnalyseWork
{
    private static Logger logger = ConsoleLogFactory.getLogger();
    
    /**
     * 分析数据库中表的顺序，给出一个按照父表（该表主键被其他表作为外键持有的）在前，子表在后的顺序的数组。如果表存在外键循环依赖，则抛出异常
     * 
     * @param tableMap
     * @return
     * @throws SQLException 如果表存在外键循环依赖，则抛出异常
     */
    public static Table[] sortSchema(Map<String, Table> tableMap) throws SQLException
    {
        long t0 = System.currentTimeMillis();
        try
        {
            calculateAllTableOrder(tableMap);
            return sortTables(tableMap);
        }
        catch (Error e)
        {
            logger.error("", e);
            return new Table[0];
        }
        finally
        {
            long t1 = System.currentTimeMillis();
            logger.debug("表排序耗时" + (t1 - t0) + "毫秒");
        }
    }
    
    /**
     * 对于每一张数据表对象，计算它在整个数据库中的循环计数
     * 
     * @param tableMap
     * @throws SQLException
     */
    private static void calculateAllTableOrder(Map<String, Table> tableMap) throws SQLException
    {
        ObjectCollect collect = new ObjectCollect(tableMap.size());
        for (Map.Entry<String, Table> entry : tableMap.entrySet())
        {
            collect.clear();
            detectedTableCycle(entry.getValue(), collect);
        }
        for (Map.Entry<String, Table> entry : tableMap.entrySet())
        {
            calculateTableOrder(entry.getValue());
        }
    }
    
    private static void calculateTableOrder(Table table)
    {
        if (table.isCalcuted())
        {
            return;
        }
        else
        {
            table.initOrderNum();
            for (Table each : table.getFatherTables())
            {
                calculateTableOrder(each);
            }
        }
    }
    
    /**
     * 表是否在一个循环路径之中
     * 
     * @param table
     * @param map
     * @param fatherLoopNum
     * @param loopPath
     * @return
     */
    private static void detectedTableCycle(Table table, ObjectCollect path)
    {
        if (path.add(table))
        {
            for (Table each : table.getFatherTables())
            {
                detectedTableCycle(each, path);
            }
        }
        else
        {
            throw new RuntimeException("表" + table.getTableName() + "存在循环外键");
        }
    }
    
    /**
     * 根据每张表的循环计数，从小到大进行排列
     * 
     * @param tableMap
     * @return
     */
    private static Table[] sortTables(Map<String, Table> tableMap)
    {
        Table[] tableInfos = new Table[tableMap.size()];
        tableMap.values().toArray(tableInfos);
        Arrays.sort(tableInfos);
        for (Table each : tableInfos)
        {
            logger.info("表" + each.getTableName() + "的排序计数是{}", each.getOrder());
        }
        return tableInfos;
    }
    
}
