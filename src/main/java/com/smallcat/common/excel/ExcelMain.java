package com.smallcat.common.excel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class ExcelMain {
	
	public static void main(String[] args) throws Exception {
		ExcelFunction excel = ExcelFactory.getExcelFunction(ExcelFactory.EXCEL_VERSION_03);
		InputStream is = new FileInputStream("C:/Users/Administrator/Desktop/Teacher_arrange_template.xls");
		
		ExcelConfigation conf = new ExcelConfigation();
		conf.setStartRowNum(1);
		conf.setTotalColNum(3);
		
		List<List<String>> datas = excel.importExcel(is, conf);
		System.out.println(datas);
	}
}
