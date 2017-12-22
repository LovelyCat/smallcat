package com.smallcat.common.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Excel2007 implements ExcelFunction {

	private static Logger logger = LoggerFactory.getLogger(Excel2003.class);

	@Override
	public List<List<String>> importExcel(InputStream is, ExcelConfigation conf) {
		List<List<String>> rtnList = new ArrayList<List<String>>();
		Workbook workBook = null;
		Sheet sheet = null;
		if (!is.markSupported()) {
			is = new PushbackInputStream(is, 8);
		}
		try {
			if (POIFSFileSystem.hasPOIFSHeader(is)
					|| POIXMLDocument.hasOOXMLHeader(is)) {
				workBook = WorkbookFactory.create(is);
			} else {
				logger.error("非法的输入流：当前输入流非OLE2流或OOXML流！");
			}
			
			if (workBook != null) {
				int numberSheet = workBook.getNumberOfSheets();
				if (numberSheet > 0) {
					sheet = workBook.getSheetAt(0);// 获取第一个工作簿(Sheet)的内容【注意根据实际需要进行修改】
				} else {
					logger.error("目标表格工作簿(Sheet)数目为0！");
					sheet = null;
				}
			}
		} catch (IOException e) {
			logger.error("创建表格工作簿对象发生IO异常！原因：" + e.getMessage(), e);
		} catch (InvalidFormatException e) {
			logger.error("非法的输入流：当前输入流非OLE2流或OOXML流！", e);
		}
		
		// 数据行长度
		int rowLength, colLength;
		
		Row row = null;
		Cell cell = null;
		String cellContent;
		StringBuilder sb = null;
		List<String> rowData = null;
		
		int startRowNum = (conf.getStartRowNum() == ExcelConfigation.DEFAULT_INT ? 0 : conf.getStartRowNum());
		int startColNum = (conf.getStartColNum() == ExcelConfigation.DEFAULT_INT ? 0 : conf.getStartColNum());
		sheet = workBook.getSheetAt(0);// 获取第一个工作簿(Sheet)的内容【注意根据实际需要进行修改】
		rowLength = sheet.getPhysicalNumberOfRows(); // 总行数
		rowLength = (conf.getTotalRowNum() == ExcelConfigation.DEFAULT_INT ? rowLength : conf.getTotalRowNum() + startRowNum);
		for (int i = startRowNum; i < rowLength; i++) {
			rowData = new ArrayList<String>();
			row = sheet.getRow(i);
			colLength = row.getLastCellNum();
			colLength = (conf.getTotalColNum() == ExcelConfigation.DEFAULT_INT ? colLength : conf.getTotalColNum() + startColNum);
			for (int j = startColNum; j < colLength; j++) {
				cell = row.getCell(j);
				if(cell == null){
					cellContent = "";
				}else{
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cellContent = cell.getStringCellValue();
				}
				rowData.add(cellContent);
			}
			// 判断改行是否为空
			sb = new StringBuilder();
			for (String rowStr : rowData) {
				sb.append(rowStr.trim());
			}
			if("".equals(sb.toString())){
				continue;
			}
			rtnList.add(rowData);
		}
		
		try {
			is.close();
		} catch (IOException e) {
			logger.error("输入流关闭失败！", e);
		}
		return rtnList;
	}

	@Override
	public Workbook exportExcel(List<ExcelFormat> formatList,
			List<Map<String, Object>> dataList) {
		// 创建Excel的工作书册 Workbook,对应到一个excel文档
		XSSFWorkbook wb = new XSSFWorkbook();

		// 创建Excel的工作sheet,对应到一个excel文档的tab
		Sheet sheet = wb.createSheet("教师列表");

		// 设置excel每列宽度
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 6000);
		sheet.setColumnWidth(2, 4000);

		// 创建字体样式
		XSSFFont font = wb.createFont();
		font.setFontName("Verdana");
		font.setBoldweight((short) 100);
		font.setFontHeight((short) 300);
		font.setColor(HSSFColor.BLACK.index);

		// 创建单元格样式
		XSSFCellStyle style = wb.createCellStyle();
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
