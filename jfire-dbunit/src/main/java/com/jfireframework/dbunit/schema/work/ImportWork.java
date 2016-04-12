package com.jfireframework.dbunit.schema.work;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.sql.DataSource;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.jfireframework.dbunit.table.Table;
import com.jfireframework.dbunit.util.SqlUtil;

public class ImportWork
{
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void importExcelFile(DataSource dataSource, Table[] sortTables, String fileName)
	{
		try (Connection importConn = dataSource.getConnection())
		{
			File excelFile = new File(Thread.currentThread().getContextClassLoader().getResource(fileName).toURI());
			DecimalFormat format = new DecimalFormat("##################.##");
			Workbook wb = WorkbookFactory.create(excelFile);
			importConn.setAutoCommit(false);
			for (Table each : sortTables)
			{
				Sheet sheet = wb.getSheet(each.getTableName());
				if (sheet == null)
				{
					continue;
				}
				int colLength = each.getColNameList().size();
				Row nameRow = sheet.getRow(0);
				String[] nameData = new String[colLength];
				for (int j = 0; j < colLength; j++)
				{
					Cell cell = nameRow.getCell(j);
					nameData[j] = getCellValue(cell, format);
				}
				String insertSql = SqlUtil.insertSqlForTable(nameData, each.getTableName());
				PreparedStatement ps = importConn.prepareStatement(insertSql);
				int rowLength = sheet.getPhysicalNumberOfRows();
				for (int i = 1; i < rowLength; i++)
				{
					Row row = sheet.getRow(i);
					String[] rowData = new String[colLength];
					for (int j = 0; j < colLength; j++)
					{
						Cell cell = row.getCell(j);
						rowData[j] = getCellValue(cell, format);
					}
					for (int column = 0; column < colLength; column++)
					{
						ps.setString(column + 1, rowData[column]);
					}
					ps.addBatch();
				}
				ps.executeBatch();
				ps.close();
			}
			importConn.commit();
		}
		catch (SQLException | URISyntaxException | InvalidFormatException | IOException e)
		{
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * 根据单元格的类型取得对应的字符串形式的值
	 * 
	 * @param cell
	 * @return
	 */
	private static String getCellValue(Cell cell, DecimalFormat format)
	{
		if (cell == null)
		{
			return null;
		}
		switch (cell.getCellType())
		{
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell))
				{
					return dateFormat.format(cell.getDateCellValue());
				}
				else
				{
					return format.format(cell.getNumericCellValue());
				}
			default:
				String value = cell.getStringCellValue();
				if (value == null || value.equals(""))
				{
					return null;
				}
				return value;
		}
	}
}
