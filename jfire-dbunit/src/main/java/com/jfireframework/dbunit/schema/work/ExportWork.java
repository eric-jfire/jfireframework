package com.jfireframework.dbunit.schema.work;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.dbunit.table.Table;

public class ExportWork
{
    private static Logger logger = ConsoleLogFactory.getLogger();
    
    /**
     * 以数据库结构为基础，生成一个填充数据用的xlsx文件，表名就是数据库表的名字，而第一行的内容则是数据库的列名
     * 
     * @param outputFile
     */
    public static void exportExcelWithoutData(Table[] sortTables)
    {
        doExport(sortTables, false);
    }
    
    public static void exportExcelWithData(Table[] sortTables)
    {
        doExport(sortTables, true);
    }
    
    private static void doExport(Table[] sorTables, boolean needData)
    {
        File outputFile = new File("test.xlsx");
        try (FileOutputStream outputStream = new FileOutputStream(outputFile))
        {
            XSSFWorkbook wb = new XSSFWorkbook();
            for (int i = 0; i < sorTables.length; i++)
            {
                Table tableInfo = sorTables[i];
                buildColNameRow(tableInfo, wb);
                if (needData)
                {
                    addRowData(tableInfo, wb);
                }
            }
            wb.write(outputStream);
            logger.info("excel文件输出完毕");
        }
        catch (IOException e)
        {
            logger.error("输出到结果文件出现错误", e);
        }
    }
    
    private static void buildColNameRow(Table table, XSSFWorkbook wb)
    {
        Sheet sheet = wb.createSheet(table.getTableName());
        Row row = sheet.createRow(0);
        int index = 0;
        for (String colName : table.getColNameList())
        {
            buildCell(index++, colName, row);
        }
    }
    
    private static void addRowData(Table table, XSSFWorkbook wb)
    {
        int rowIndex = 1;
        Sheet sheet = wb.getSheet(table.getTableName());
        for (com.jfireframework.dbunit.table.Row tableRow : table.getRowArray())
        {
            Row sheetRow = sheet.createRow(rowIndex);
            String[] data = tableRow.getData();
            for (int colIndex = 0; colIndex < data.length; colIndex++)
            {
                buildCell(colIndex, data[colIndex], sheetRow);
            }
            rowIndex++;
        }
    }
    
    private static void buildCell(int colIndex, String cellValue, Row row)
    {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(cellValue);
    }
}
