package com.smallcat.common.excel;

public final class ExcelFactory {
	
	public static final String EXCEL_VERSION_03 = "2003";
	public static final String EXCEL_VERSION_07 = "2007";
	
	public static ExcelFunction getExcelFunction(String version){
		if(EXCEL_VERSION_03.equals(version)){
			return new Excel2003();
		}else if(EXCEL_VERSION_07.equals(version)){
			return new Excel2007();
		}else{
			return null;
		}
	}
}

