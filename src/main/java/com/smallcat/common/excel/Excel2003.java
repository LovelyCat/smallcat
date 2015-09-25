package com.smallcat.common.excel;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

public class Excel2003 implements ExcelFunction{

	@Override
	public List<List<String>> importExcel(MultipartFile file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Workbook exportExcel(List<ExcelFormat> formatList,
			List<Map<String, Object>> dataList) {
		// TODO Auto-generated method stub
		return null;
	}

}
