package com.smallcat.common.excel;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

public class Excel2007 implements ExcelFunction {

	@Override
	public List<List<String>> importExcel(MultipartFile file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Workbook exportExcel(List<ExcelFormat> formatList,
			List<Map<String, Object>> dataList) {
		// 创建Excel的工作书册 Workbook,对应到一个excel文档
		HSSFWorkbook wb = new HSSFWorkbook();

		// 创建Excel的工作sheet,对应到一个excel文档的tab
		HSSFSheet sheet = wb.createSheet("教师列表");

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
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 500);// 设定行的高度

		HSSFCell cell;
		for (int i = 0; i < formatList.size(); i++) {
			// 创建一个Excel的单元格
			cell = row.createCell(i);
			// 给Excel的单元格设置样式和赋值
			cell.setCellStyle(style);
			cell.setCellValue(formatList.get(i).getColTitleName());
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
