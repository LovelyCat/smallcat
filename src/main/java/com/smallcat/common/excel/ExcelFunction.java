package com.smallcat.common.excel;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelFunction {

	public List<List<String>> importExcel(MultipartFile file);
	
	public Workbook exportExcel(List<ExcelFormat> formatList,
			List<Map<String, Object>> dataList);
	
}
