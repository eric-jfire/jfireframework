package com.jfireframework.dbunit.schema;

import java.sql.SQLException;
import java.util.HashMap;
import javax.sql.DataSource;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.dbunit.schema.work.BuckupWork;
import com.jfireframework.dbunit.schema.work.BuildSchemaStructureWork;
import com.jfireframework.dbunit.schema.work.CleanWork;
import com.jfireframework.dbunit.schema.work.ExportWork;
import com.jfireframework.dbunit.schema.work.ImportWork;
import com.jfireframework.dbunit.schema.work.RestoreWork;
import com.jfireframework.dbunit.table.Table;
import com.jfireframework.dbunit.util.Util;

public class DbUnit
{
    public static final int        SAVE_IN_MEM = 1;
    public static final int        ANALYSE     = 2;
    private Logger                 logger      = ConsoleLogFactory.getLogger();
    private HashMap<String, Table> tableMap    = new HashMap<String, Table>();
    private DataSource             dataSource;
    private Table[]                sortTables;
    
    public DbUnit(int workmode, DataSource dataSource)
    {
        try
        {
            this.dataSource = dataSource;
            sortTables = BuildSchemaStructureWork.buildSchemaStructure(dataSource, tableMap);
            switch (workmode)
            {
                case ANALYSE:
                    logger.info("表结构建立完毕");
                    break;
                case SAVE_IN_MEM:
                    BuckupWork.buckupDbData(dataSource, tableMap);
                    logger.info("表结构建立完毕，数据保存在内存");
                    break;
            }
        }
        catch (SQLException e)
        {
            logger.error("建立数据库结构上下文环境失败", e);
        }
    }
    
    public void exportExcelWithoutData()
    {
        ExportWork.exportExcelWithoutData(sortTables);
        logger.info("生成电子表格结构");
    }
    
    public void exportExcelWithData()
    {
        ExportWork.exportExcelWithData(sortTables);
        logger.info("导出数据库数据到电子表格");
    }
    
    public void clearSchemaData()
    {
        CleanWork.clearDbData(dataSource, sortTables);
        logger.info("数据库数据清除完毕");
    }
    
    public void restoreSchemaData()
    {
        RestoreWork.restoreDbData(dataSource, sortTables);
        logger.info("数据库数据还原完成，内存占用：" + Util.usedMemory());
    }
    
    public void importExcelFile()
    {
        ImportWork.importExcelFile(dataSource, sortTables, "test.xlsx");
        logger.info("excel数据导入完毕,内存占用：" + Util.usedMemory());
    }
    
    public void importExcelFile(String fileName)
    {
        ImportWork.importExcelFile(dataSource, sortTables, fileName);
        logger.info("excel数据导入完毕,内存占用：" + Util.usedMemory());
    }
    
    public HashMap<String, Table> getTableMap()
    {
        return tableMap;
    }
    
}
