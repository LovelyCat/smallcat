package com.smallcat.common.excel;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ExcelFunction {
	
	public static Logger logger = LoggerFactory.getLogger(ExcelFunction.class);

	public List<List<String>> importExcel(InputStream is, ExcelConfigation conf);
	
	public Workbook exportExcel(List<ExcelFormat> formatList,
			List<Map<String, Object>> dataList);
	
}
