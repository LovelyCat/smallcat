package com.smallcat.common.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Excel2003 implements ExcelFunction{

	private static Logger logger = LoggerFactory.getLogger(Excel2003.class);

	@Override
	public List<List<String>> importExcel(InputStream is, ExcelConfigation conf) {
		List<String> rowData = null;
		List<List<String>> rtnList = new ArrayList<List<String>>();
		
		HSSFWorkbook hssfWorkbook = null;
		HSSFSheet hssfSheet = null;
		HSSFRow hssfRow = null;
		HSSFCell nameCell = null;
		// 数据行长度
		int rowLength, colLength;
		int startRowNum = (conf.getStartRowNum() == ExcelConfigation.DEFAULT_INT ? 0 : conf.getStartRowNum());
		int startColNum = (conf.getStartColNum() == ExcelConfigation.DEFAULT_INT ? 0 : conf.getStartColNum());
		
		String cellContent = "";
		StringBuilder sb = null;
		
		try {
			hssfWorkbook = new HSSFWorkbook(is);
		} catch (Exception e) {
			return new ArrayList<List<String>>();
		}
		
		if(hssfWorkbook.getNumberOfSheets() > 0){
			//执行第一页
			hssfSheet = hssfWorkbook.getSheetAt(0);
			
			rowLength = hssfSheet.getLastRowNum();
			rowLength = (conf.getTotalRowNum() == ExcelConfigation.DEFAULT_INT ? rowLength + startRowNum : conf.getTotalRowNum());
			// 循环行Row
			for (int rowNum = startRowNum; rowNum < rowLength; rowNum++) {
				try {
					hssfRow = hssfSheet.getRow(rowNum);
					if(hssfRow == null){
						break;
					}
					rowData = new ArrayList<String>();
					// 数据列长度
					colLength = hssfRow.getLastCellNum();
					colLength = (conf.getTotalColNum() == ExcelConfigation.DEFAULT_INT ? colLength + startColNum : conf.getTotalColNum());
					for (int colNum = startColNum; colNum < colLength; colNum ++) {
						nameCell = hssfRow.getCell(colNum);
						if(nameCell == null){
							cellContent = "";
						}else{
							nameCell.setCellType(Cell.CELL_TYPE_STRING);
							cellContent = nameCell.getStringCellValue();
						}
						rowData.add(cellContent);
					}
					
					// 判断改行是否为空
					sb = new StringBuilder();
					for (String row : rowData) {
						sb.append(row.trim());
					}
					if("".equals(sb.toString())){
						continue;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				rtnList.add(rowData);
			}
		}
		
		try {
			hssfWorkbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return rtnList;
	}

	@Override
	public Workbook exportExcel(List<ExcelFormat> formatList,
			List<Map<String, Object>> dataList) {
		// 创建Excel的工作书册 Workbook,对应到一个excel文档
		HSSFWorkbook wb = new HSSFWorkbook();

		// 创建Excel的工作sheet,对应到一个excel文档的tab
		Sheet sheet = wb.createSheet("教师列表");

		// 设置excel每列宽度
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 6000);
		sheet.setColumnWidth(2, 4000);

		// 创建字体样式
		HSSFFont font = wb.createFont();
		font.setFontName("Verdana");
		font.setBoldweight((short) 100);
		font.setFontHeight((short) 300);
		font.setColor(HSSFColor.BLACK.index);

		// 创建单元格样式
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(HSSFColor.WHITE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		// 设置边框
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);

		style.setFont(font);// 设置字体

		// 创建Excel的sheet的一行
		Row row = sheet.createRow(0);
		row.setHeight((short) 500);// 设定行的高度

		Cell cell;
		if(formatList != null && formatList.size() > 0){
			for (int i = 0; i < formatList.size(); i++) {
				// 创建一个Excel的单元格
				cell = row.createCell(i);
				// 给Excel的单元格设置样式和赋值
				cell.setCellStyle(style);
				cell.setCellValue(formatList.get(i).getColTitleName());
			}
		}
		
		if(dataList == null || dataList.size() <= 0){
			logger.error("输入的数据为空");
			return null;
		}

		// 创建字体样式
		font = wb.createFont();
		font.setFontName("Verdana");
		font.setBoldweight((short) 100);
		font.setFontHeight((short) 200);
		font.setColor(HSSFColor.BLACK.index);

		// 创建单元格样式
		style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(HSSFColor.WHITE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		// 设置边框
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);

		style.setFont(font);// 设置字体

		int length = dataList.size();

		Map<String, Object> rowData = null;
		ExcelFormat excelFormat = null;
		for (int i = 0; i < length; i++) {
			rowData = dataList.get(i);
			row = sheet.createRow(i + 1);
			row.setHeight((short) 500);
			
			for(int j=0; j<formatList.size(); j++){
				excelFormat = formatList.get(j);
				cell = row.createCell(j);
				
				cell.setCellStyle(style);
				cell.setCellValue(rowData.get(excelFormat.getColMethodName()).toString());
			}
		}
		
		return wb;
	}

}
