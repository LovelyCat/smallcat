package com.smallcat.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Currency.java description :
 * 货币对象
 * @version 1.0
 */
public final class Currency {

	/** 货币码 */
	private String code;
	/** 国际化标识 */
	private String i18nId;
	/** 辅币位数 */
	private Integer fraction;

	public Currency() {

	}

	private Currency(String currenyCode) {
		this.code = currenyCode;
	}

	private Currency(String code, String i18nId, int fraction) {

		this.code = code;
		this.i18nId = i18nId;
		this.fraction = new Integer(fraction);
	}

	/** 货币码 */
	public String getCode() {
		return this.code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	/** 辅币位数 */
	public Integer getFraction() {
		return this.fraction;
	}

    public void setFraction(Integer fraction) {
        this.fraction = fraction;
    }
    
	/** 国际化标识 */
	public String getI18nId() {
		return this.i18nId;
	}

    public void setI18nId(String i18nId) {
        this.i18nId = i18nId;
    }
    
	public String toString() {
		return this.code == null ? "" : this.code;
	}

	public boolean equals(Object value) {
		if (value instanceof Currency) {
			Currency currency = (Currency) value;
			return currency.code.equals(this.code);
		}
		return false;
	}

	public int hashCode() {
		return (int) (this.code.hashCode() ^ 3116647775521641945L);
	}
	
	public static boolean isSupportedCode(String code) {
		return Cache.containsKey(code);
	}
	
	public static boolean isSupportedI18NCode(String icode) {
		return interCache.containsKey(icode);
	}
	
	/**
	 * 根据货币码 获得 货币对象，如果货币码不存在以该货币码创建的货币对象
	 * @param code
	 * @return
	 */
	public final static Currency instance(String code) {
		
		if(code == null || code.equals("")) 
			return Cache.get("001");
		Currency currency = Cache.get(code);
		if (currency != null) {
			return currency;
		}
		currency = new Currency(code);
		currency.fraction = new Integer(2);
		return currency;
	}
	
	
	/**
	 * 根据货币码 获得 货币对象，如果货币码不存在返回null
	 * @param code
	 * @return
	 */
	public final static Currency instance2(String code) {
		
		if(code == null || code.equals("")) 
			return null;
		Currency currency = Cache.get(code);
		if (currency != null) {
			return currency;
		}else return null;		
	}
	
	

	/**
	 * 根据国际化标识 获得 货币对象，如果国际化标识不存在返回人民币
	 * @param code
	 * @return
	 */
	public final static Currency instanceInter(String code) {
		if(code == null || code.equals("")) 
			return null;
		
		Currency currency = interCache.get(code);
		if (currency != null) {
			return currency;
		}
		return interCache.get("CNY");
	}

	/**
	 * 根据国际化标识 获得 货币对象，如果国际化标识不存在返回null
	 * @param code
	 * @return
	 */
	public final static Currency instanceInter2(String code) {
		if(code == null || code.equals("")) 
			return null;
		
		Currency currency = (Currency) interCache.get(code);
		if (currency != null) {
			return currency;
		}else return null;
		
	}	
	
	/**
	 * 根据 货币码 或 国际货币码 获得货币对象
	 * @param code
	 * @return
	 */
	public final static Currency instanceALL(String code) {
		if(code == null || code.equals("")) return (Currency) Cache.get("001");
		Map<String, Currency> allCur = new HashMap<String, Currency>();
		allCur.putAll(Cache);
		allCur.putAll(interCache);
		Currency currency = (Currency) allCur.get(code);
		if (currency != null) return currency;
		currency = new Currency(code);
		currency.fraction = new Integer(2);
		return currency;
	}
	
	/**
	 * 根据 货币码 或 国际货币码 获得货币对象
	 * @param code
	 * @return
	 */
	public final static Currency instanceALL2(String code) {
		Map<String, Currency> allCur = new HashMap<String, Currency>();
		allCur.putAll(Cache);
		allCur.putAll(interCache);
		Currency currency = (Currency) allCur.get(code);
		return currency;
	}
	
    /**
     * 货币代码转成国际代码
     * @param code
     * @return
     */
    public final static String code2inter(String code) {
        if(code == null || code.equals("")) 
            return null;
        
        Currency cur = Currency.instance2(code);
        if(cur == null)
            return null;
        else
            return cur.getI18nId();
    }
    
    /**
     * 国际代码转成货币代码
     * @param inter
     * @return
     */
    public final static String inter2code(String inter){
    	if(inter == null || inter.equals(""))
    		return null;
    	
    	Currency cur = Currency.instanceInter2(inter);
    	if(cur == null)
    		return null;
    	else
    		return cur.getCode();
    }
    
	private final static Map<String, Currency> Cache = new HashMap<String, Currency>();

	static {
		Cache.put("000", new Currency("000", "000", 0)); // 货币不存在
		Cache.put("001", new Currency("001", "CNY", 2)); // 人民币
		Cache.put("012", new Currency("012", "GBP", 2)); // 英镑
		Cache.put("013", new Currency("013", "HKD", 2)); // 港币
		Cache.put("014", new Currency("014", "USD", 2)); // 美元
		Cache.put("015", new Currency("015", "CHF", 2)); // 瑞士法郎
		Cache.put("016", new Currency("016", "DEM", 2)); // 德国马克
		Cache.put("017", new Currency("017", "FRF", 2)); // 法国法郎
		Cache.put("018", new Currency("018", "SGD", 2)); // 新加坡元
		Cache.put("020", new Currency("020", "NLG", 2)); // 荷兰盾
		Cache.put("021", new Currency("021", "SEK", 2)); // 瑞典克郎
		Cache.put("022", new Currency("022", "DKK", 2)); // 丹麦克郎
		Cache.put("023", new Currency("023", "NOK", 2)); // 挪威克郎
		Cache.put("024", new Currency("024", "ATS", 2)); // 奥地利先令
		Cache.put("025", new Currency("025", "BEF", 0)); // 比利时法郎
		Cache.put("026", new Currency("026", "ITL", 0)); // 意大利里拉
		Cache.put("027", new Currency("027", "JPY", 0)); // 日元
		Cache.put("028", new Currency("028", "CAD", 2)); // 加拿大元
		Cache.put("029", new Currency("029", "AUD", 2)); // 澳大利亚元
		Cache.put("034", new Currency("034", "XAU", 2)); // 黄金（盎司）
		Cache.put("035", new Currency("035", "GLD", 0)); // 黄金（克）
        Cache.put("036", new Currency("036", "XAG",2));  //美元银
		Cache.put("038", new Currency("038", "EUR", 2)); // 欧元
		Cache.put("042", new Currency("042", "FIM", 2)); // 芬兰马克
		Cache.put("056", new Currency("056", "IDR", 2)); // 印尼盾
		//BOCNET5.0二期 调整越南盾辅币位数为0  cuiyk
		Cache.put("064", new Currency("064", "VND", 0)); // 越南盾
        Cache.put("068", new Currency("068", "SLV",0));   //人民币银
		Cache.put("081", new Currency("081", "MOP", 2)); // 澳元
		Cache.put("082", new Currency("082", "PHP", 2)); // 菲律宾比索
		Cache.put("084", new Currency("084", "THB", 2)); // 泰国株
		Cache.put("087", new Currency("087", "NZD", 2)); // 新西兰元
		Cache.put("088", new Currency("088", "KRW", 0)); // 韩元
		Cache.put("095", new Currency("095", "CLS", 2)); // 清算瑞士法郎
		
		//modify by cuiyk  卢布由999--->072  BOCNET3.0 2期海外12家分行对公推广
		Cache.put("072", new Currency("072", "RUR", 2)); // 清算卢布
		Cache.put("zzz", new Currency("zzz", "ZZZ", 2)); // 其他
		//为避免和096=美元指数冲突,将阿联酋迪拉姆改为777
		Cache.put("777", new Currency("777", "AED", 2)); // 阿联酋迪拉姆
		Cache.put("126", new Currency("126", "ARP", 2)); // 阿根廷比索
		Cache.put("134", new Currency("134", "BRL", 2)); // 巴西雷亚尔
		Cache.put("053", new Currency("053", "EGP", 2)); // 埃及磅
		Cache.put("085", new Currency("085", "INR", 2)); // 印度卢比
		Cache.put("057", new Currency("057", "JOD", 2)); // 约旦第纳尔
		Cache.put("179", new Currency("179", "MNT", 2)); // 蒙古图格里克
		Cache.put("032", new Currency("032", "MYR", 2)); // 马拉西亚林吉特  马币
		Cache.put("076", new Currency("076", "NGN", 2)); // 尼日尼亚奈拉
		Cache.put("062", new Currency("062", "ROL", 2)); // 罗马尼亚列伊
		Cache.put("093", new Currency("093", "TRL", 2)); // 土耳其里拉
		Cache.put("246", new Currency("246", "UAH", 2)); // 乌克兰格里夫纳
		Cache.put("070", new Currency("070", "ZAR", 2)); // 南非兰特
		//add  by cuiyk
		Cache.put("065", new Currency("065", "HUF", 2)); //匈牙利福林
		Cache.put("101", new Currency("101", "KZT", 2)); //哈萨克斯坦坚戈
		Cache.put("080", new Currency("080", "ZMK", 2)); //赞比亚克瓦查
		Cache.put("045", new Currency("045", "XPT", 2)); //美元铂金   P307新增账户贵金属 zwj3533
		Cache.put("131", new Currency("131", "BND", 2)); //文莱币
		Cache.put("039", new Currency("039", "BWP", 2)); //博茨瓦纳普拉
		Cache.put("253", new Currency("253", "ZMW", 2)); //赞比亚新币种 克瓦查
		
		//add by yangda
		Cache.put("166", new Currency("166", "KHR", 2)); // 柬埔寨货币  瑞尔
		
		//added by linxn
		Cache.put("196", new Currency("196", "RUB", 2)); // 卢布
		
		//add by zwj3533 P307新增账户贵金属
		Cache.put("841", new Currency("841", "XPD", 2));  //美元钯金（盎司）
		Cache.put("845", new Currency("845", "PLT", 0));  //人民币铂金（克）
		Cache.put("844", new Currency("844", "PLD", 0));  //人民币钯金
		
		//add by mawenchao3529
		Cache.put("213", new Currency("213", "TWD", 2));  //新台湾元
	}

	private final static Map<String, Currency> interCache = new HashMap<String, Currency>();

	static {
		interCache.put("000", new Currency("000", "000", 0)); // 货币不存在
		interCache.put("CNY", new Currency("001", "CNY", 2)); // 人民币
		interCache.put("GBP", new Currency("012", "GBP", 2)); // 英镑
		interCache.put("HKD", new Currency("013", "HKD", 2)); // 港币
		interCache.put("USD", new Currency("014", "USD", 2)); // 美元
		interCache.put("CHF", new Currency("015", "CHF", 2)); // 瑞士法郎
		
		interCache.put("DEM", new Currency("016", "DEM", 2)); // 德国马克
		interCache.put("FRF", new Currency("017", "FRF", 2)); // 法国法郎
		interCache.put("NLG", new Currency("020", "NLG", 2)); // 荷兰盾
		interCache.put("ATS", new Currency("024", "ATS", 2)); // 奥地利先令
		interCache.put("BEF", new Currency("025", "BEF", 0)); // 比利时法郎
		interCache.put("ITL", new Currency("026", "ITL", 0)); // 意大利里拉
		interCache.put("XAU", new Currency("034", "XAU", 2)); // 黄金（盎司）
		interCache.put("GLD", new Currency("035", "GLD", 0)); // 黄金（克）
		interCache.put("XAG", new Currency("036","XAG",2));  //美元银
		interCache.put("SLV", new Currency("068","SLV",0));   //人民币银
		interCache.put("FIM", new Currency("042", "FIM", 2)); // 芬兰马克
		interCache.put("PHP", new Currency("082", "PHP", 2)); // 菲律宾比索
				
		interCache.put("SGD", new Currency("018", "SGD", 2)); // 新加坡元
		interCache.put("SEK", new Currency("021", "SEK", 2)); // 瑞典克郎
		interCache.put("DKK", new Currency("022", "DKK", 2)); // 丹麦克郎
		interCache.put("NOK", new Currency("023", "NOK", 2)); // 挪威克郎
		interCache.put("JPY", new Currency("027", "JPY", 0)); // 日元
		interCache.put("CAD", new Currency("028", "CAD", 2)); // 加拿大元
		interCache.put("AUD", new Currency("029", "AUD", 2)); // 澳大利亚元
		interCache.put("EUR", new Currency("038", "EUR", 2)); // 欧元
		interCache.put("IDR", new Currency("056", "IDR", 2)); // 印尼盾
		//BOCNET5.0二期 调整越南盾辅币位数为0 cuiyk
		interCache.put("VND", new Currency("064", "VND", 0)); // 越南盾		
		interCache.put("MOP", new Currency("081", "MOP", 2)); // 澳元
		interCache.put("THB", new Currency("084", "THB", 2)); // 泰国株
		interCache.put("NZD", new Currency("087", "NZD", 2)); // 新西兰元
		interCache.put("KRW", new Currency("088", "KRW", 0)); // 韩元
		interCache.put("CLS", new Currency("095", "CLS", 2)); // 清算瑞士法郎
        //modify by cuiyk  卢布由999--->072  BOCNET3.0 2期海外12家分行对公推广
		interCache.put("RUR", new Currency("072", "RUR", 2)); // 清算卢布
		
		//为避免和096=美元指数冲突,将阿联酋迪拉姆改为777
		interCache.put("AED", new Currency("777", "AED", 2)); // 阿联酋迪拉姆
		interCache.put("ARP", new Currency("126", "ARP", 2)); // 阿根廷比索
		interCache.put("BRL", new Currency("134", "BRL", 2)); // 巴西雷亚尔
		interCache.put("EGP", new Currency("053", "EGP", 2)); // 埃及磅
		interCache.put("INR", new Currency("085", "INR", 2)); // 印度卢比
		interCache.put("JOD", new Currency("057", "JOD", 2)); // 约旦第纳尔
		interCache.put("MNT", new Currency("179", "MNT", 2)); // 蒙古图格里克
		interCache.put("MYR", new Currency("032", "MYR", 2)); // 马拉西亚林吉特
		interCache.put("NGN", new Currency("076", "NGN", 2)); // 尼日尼亚奈拉
		interCache.put("ROL", new Currency("062", "ROL", 2)); // 罗马尼亚列伊
		interCache.put("TRL", new Currency("093", "TRL", 2)); // 土耳其里拉
		interCache.put("UAH", new Currency("246", "UAH", 2)); // 乌克兰格里夫纳
		interCache.put("ZAR", new Currency("070", "ZAR", 2)); // 南非兰特
		
		//add by cuiyk
		interCache.put("HUF", new Currency("065", "HUF", 2)); //匈牙利福林
		interCache.put("KZT", new Currency("101", "KZT", 2)); //哈萨克斯坦坚戈
		interCache.put("ZMK", new Currency("080", "ZMK", 2)); //赞比亚克瓦查
		interCache.put("XPT", new Currency("045", "XPT", 2)); //美元铂金（盎司）   P307新增账户贵金属 zwj3533
		interCache.put("BND", new Currency("131", "BND", 2)); //文莱币
		interCache.put("BWP", new Currency("039", "BWP", 2)); //博茨瓦纳普拉
		interCache.put("ZMW", new Currency("253", "ZMW", 2)); //赞比亚新币种 克瓦查
		
		
		//add by yangda 
		interCache.put("KHR", new Currency("166", "KHR", 2)); // 柬埔寨货币  瑞尔
		
		//added by linxn
		interCache.put("RUB", new Currency("196", "RUB", 2)); // 卢布
		
		//add by zwj3533 P307新增账户贵金属
		interCache.put("XPD", new Currency("841", "XPD", 2));  //美元钯金
		interCache.put("PLT", new Currency("845", "PLT", 0));  //人民币铂金（克）
		interCache.put("PLD", new Currency("844", "PLD", 0));  //人民币钯金
		
		//add by mawenchao3529
		interCache.put("TWD", new Currency("213", "TWD", 2));  //新台湾元
	}

}