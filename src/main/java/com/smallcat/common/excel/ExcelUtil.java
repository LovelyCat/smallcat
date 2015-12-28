package com.smallcat.common.excel;

import java.io.FileOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelUtil {

	/** 设置合并后的单元格的样式 **/
	public static void setRegionStyle(CellStyle cellStyle, CellRangeAddress region, Sheet sheet){  
		Row row = null;
		Cell cell = null;
        for(int rowNum = region.getFirstRow(); rowNum <= region.getLastRow(); rowNum ++){  
            row = sheet.getRow(rowNum);  
            if(row == null) row = sheet.createRow(rowNum);  
            for(int colNum = region.getFirstColumn(); colNum <= region.getLastColumn(); colNum ++){  
                cell = row.getCell(colNum);  
                if( cell == null){  
                    cell = row.createCell(colNum);  
                    cell.setCellValue("");  
                }  
                cell.setCellStyle(cellStyle);  
            }  
        }  
    }  
	
	/** 合并单元格 **/
	public static void mergeCell(Sheet sheet, CellStyle style){
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直     
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平     
        
        Row row = null;
        Cell cell = null;
        for(int i=0; i<9; i++){
        	sheet.createRow(i);
        }
        row = sheet.getRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));
        cell = row.createCell(0);     
        cell.setCellValue("merging1"); // 跨单元格显示的数据     
        
        row = sheet.getRow(3);
        sheet.addMergedRegion(new CellRangeAddress(3, 4, 0, 0));
        cell = row.createCell(0);     
        cell.setCellValue("merging2"); // 跨单元格显示的数据     
	}
	
	/** 生成Excel结果（本地文件，网页下载文件） **/
	public static void createExcelResult(HSSFWorkbook wb,
			HttpServletResponse response) throws Exception {
		/*** 这里是问题的关键，将这个工作簿写入到一个流中就可以输出相应的名字，这里需要写路径就ok了。 **/
		FileOutputStream fileOut = new FileOutputStream("D:/workbook.xls");
		wb.write(fileOut);
		fileOut.close();

		/** 第二种是输出到也面中的excel名称 **/
		String fileName = "栏目统计表";
		
		response.reset();
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ new String(fileName.getBytes("gb2312"), "ISO-8859-1") + ".xls");
		ServletOutputStream outStream = null;

		try {
			outStream = response.getOutputStream();
			wb.write(outStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			outStream.close();
		}
	}
}
