package com.smallcat.common.excel;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

public interface ExcelFunction {
	
	public static Logger logger = Logger.getLogger(ExcelFunction.class);

	public List<List<String>> importExcel(InputStream is, ExcelConfigation conf);
	
	public Workbook exportExcel(List<ExcelFormat> formatList,
			List<Map<String, Object>> dataList);
	
}
