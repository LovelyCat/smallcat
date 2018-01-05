package com.smallcat.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



/**
 * MoneyUtil.java
 * 金额处理相关工具
 * 
 * @version 1.0
 * @author xulc
 * Written Date: 2007-7-31
 * 
 * Modified By: 
 * Modified Date:
 */
public class MoneyUtil 
{
    private final static String NO_DECPOS = "#,##0";//辅币位数0
    private final static String ONE_DECPOS = "#,##0.0";//辅币位数1
    private final static String TWO_DECPOS = "#,##0.00";//辅币位数2
    private final static String THR_DECPOS = "#,##0.000";//辅币位数3

    
    private static String ChnDigiStr[] =
        new String[] { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };

    private static String ChnDiviStr[] =
        new String[] {"","拾","佰","仟",   "万","拾","佰","仟",    "亿","拾","佰","仟",    "万","拾","佰","仟",    "亿","拾","佰","仟",    "万","拾","佰","仟" };
        


    /**
     * 转为BigDecimal[]
     * @param object
     * @return
     */
    public static BigDecimal[] getBigDecimalArray(Object object) {
        if (object instanceof BigDecimal[])
            return (BigDecimal[]) object;
        if (object instanceof BigDecimal) {
            BigDecimal tmpStrs[] = new BigDecimal[1];
            tmpStrs[0] = (BigDecimal) object;
            return tmpStrs;
        }
        return null;
    }

    /**
     * 转为BigDecimal
     * @param object：BigDecimal，String or Number
     * @return
     */
    public static BigDecimal getBigDecimal(Object object) {
        if (object == null)
            return null;

        BigDecimal result;

        if (object instanceof BigDecimal)
            result = (BigDecimal) object;
        else if (object instanceof String) 
        {
            String tmp = (String) object;
            
            if (tmp.indexOf(",") != -1)
                tmp = tmp.replaceAll(",","");
                
            result = new BigDecimal(tmp);
        } else if (object instanceof Number) {
            result = new BigDecimal(((Number) object).doubleValue());
        } else
            throw new IllegalArgumentException(object.toString());

        return result;

    }

    public static BigDecimal round(BigDecimal v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal one = new BigDecimal("1");
        return v.divide(one, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 根据币种格式化金额
     * 
     * @param money 金额
     * @param currency Currency对象
     * @author xulc
     * @return
     */
    public static String formatMoney(Object money,Currency currency) 
    {
        if (money == null || currency == null)
            return null;

        try
        {
            money = getBigDecimal(money);
        }
        catch(Exception e)
        {
            return (String)money;
        }

        String pattern = getPattern(currency.getFraction());

        try 
        {
            DecimalFormat format = (DecimalFormat)NumberFormat.getInstance(Locale.US);
            format.applyPattern(pattern);

            return format.format(money);
        }
        catch(Exception e) 
        {
            return null;
        }
    }

    /**
     * 从LIST中分币种汇总金额,并按照币种排序
     * 
     * @author:许笠晨
     * 
     * @param onePageList
     * @param curCdeKey 币种字段的key值
     * @param moneyKey 金额字段的key值
     */
    public static List getSumarryInList(List onePageList,String curCdeKey,String moneyKey)
    {
        if (StringUtil.isNullOrEmpty(onePageList))
            return null;
        
        List resList = new ArrayList();
        Map addMap = null;
        
        for(int i = 0; i < onePageList.size(); i++)
        {
            Map tmp = (Map)onePageList.get(i);

            if (tmp == null || StringUtil.isNullOrEmpty(tmp.get(curCdeKey)) || StringUtil.isNullOrEmpty(tmp.get(moneyKey)))
                continue;

            if (i == 0)
            {
                Map once = new HashMap();
                once.putAll(tmp);

                resList.add(once);
                continue;
            }

            boolean addFlag = false;

            for(int j = 0; j < resList.size(); j++)
            {
                Map resTmp = (Map)resList.get(j);

                if (tmp.get(curCdeKey).toString().equals(resTmp.get(curCdeKey).toString()))
                {
                    resTmp.put(moneyKey,doMath(resTmp.get(moneyKey).toString(),tmp.get(moneyKey).toString(),1,2));
                    addFlag = true;
                    break;
                }
                else
                    continue;
            }
            
            if (!addFlag)
            {
                addMap = new HashMap();
                addMap.putAll(tmp);
                
                resList.add(addMap);
                addFlag = false;
            }
        }
        
        Collections.sort(resList,new MyComparator(curCdeKey));
        Collections.reverse(resList);
        
        return resList;
    }


    /**
     * 将List中的某个字段相加
     * 
     * @author:许笠晨
     * 
     * @param list
     * @param key 要相加的字段的key值
     */
    public static String getSumarryByKey(List list,String key)
    {
        String res = "0";
        
        for(int i = 0; i < list.size(); i++)
        {
            Map tmp = (Map)list.get(i);
            
            if (StringUtil.isNullOrEmpty(tmp.get(key)))
                continue;
                
            try
            {
                res = doMath(res,tmp.get(key).toString(),1,2);
            }catch(Exception e){}

        }
        
        return res;
    }

    /** 功能: 将小写金额转换为大写金额<br>
     *  @param str str: 小写金额字符串<br>
     *  @return String 大写金额字符串<br>
     *  @version:    1.0<br>
     * @author name：zhoujianguo <br>
     * Written Date：2007/05/23<br> 
     */
    public static String money2ChnText(String str) {
        //原来有的程序处理了格式化字符串，有的没有处理，为了兼容，统一把格式化逗号替换掉
        str = str.replaceAll(",", "");
        String result = "";
        double val = 0.0;
        try{
            val =  (new Double(str)).doubleValue();
        }
        catch(Exception e){
            return "";
            
        }
        

        String TailStr = "";
        long fraction, integer;
        int jiao, fen;

        if (val < 0) {
            val = -val;
        }
        if (val > 9999999999999.99 || val < -9999999999999.99)
            return "";
        // 四舍五入到分  
        long temp = Math.round(val * 100);
        integer = temp / 100;
        fraction = temp % 100;
        jiao = (int) fraction / 10;
        fen = (int) fraction % 10;
        if (jiao == 0 && fen == 0) {
            TailStr = "整";
            result = PositiveInteger2ChnStr(String.valueOf(integer)) + "元" + TailStr;
        } else {
            TailStr = ChnDigiStr[jiao];
            if (jiao != 0)
                TailStr += "角";
            if (integer == 0 && jiao == 0) // 零元后不写零几分
                TailStr = "";
            if (fen != 0)
                TailStr += ChnDigiStr[fen] + "分";

            if(integer == 0){
                result = TailStr;
            }
            else{
                result = PositiveInteger2ChnStr(String.valueOf(integer)) + "元" + TailStr;
            }
        }

        //return PositiveInteger2ChnStr(String.valueOf(integer)) + "元" + TailStr;
        return result;
    }

    public static String PositiveInteger2ChnStr(String NumStr) { // 输入字符串必须正整数，只允许前导空格(必须右对齐)，不宜有前导零
        String RMBStr = "";
        boolean lastzero = false;
        boolean hasvalue = false; // 亿、万进位前有数值标记
        boolean beginzero = false;
        int len, n;
        len = NumStr.length();
        if (len > 15)
            return "";
        for (int i = len - 1; i >= 0; i--) {
            if (NumStr.charAt(len - i - 1) == ' ')
                continue;
            n = NumStr.charAt(len - i - 1) - '0';
            if (n < 0 || n > 9)
                return "";
            if (n != 0) {
                if(beginzero == true){
                    RMBStr += "零";
                    beginzero = false;
                }
                if (lastzero)
                    RMBStr += ChnDigiStr[0]; // 若干零后若跟非零值，只显示一个零
                // 除了亿万前的零不带到后面
                /*if (!(n == 1 && (i % 4) == 1 && i == len - 1)) // 十进位处于第一位不发壹音modi 20070529*/
                RMBStr += ChnDigiStr[n];
                RMBStr += ChnDiviStr[i]; // 非零值后加进位，个位为空
                hasvalue = true; // 置万进位前有值标记

            } else {
                beginzero = true;
                if ((i % 8) == 0
                    || ((i % 8) == 4 && hasvalue)) // 亿万之间必须有非零值方显示万
                    RMBStr += ChnDiviStr[i]; // “亿”或“万”
            }
            if (i % 8 == 0)
                hasvalue = false; // 万进位前有值标记逢亿复位
            lastzero = (n == 0) && (i % 4 != 0);
        }

        if (RMBStr.length() == 0)
            return ChnDigiStr[0]; // 输入空字符或"0"，返回"零"
        //System.out.println("before replace:" + RMBStr);
        RMBStr = RMBStr.replaceAll("零零","零");
        //System.out.println("after  replace:" + RMBStr);
        return RMBStr;
    }

    /**
     *  Description：对于两个字符串型的数字做运算
     *
     *  @author:许笠晨
     *  @param parameter str1:+表示加数;-表示减数;*表示乘数;/表示除数;
     *                   str2:+表示被加数;-表示被减数;*表示被乘数;/表示被除数;
     *                   type:1表示做加法;2表示做减法;3表示做乘法;4表示做除法;
     *                   llen:表示保留小数的位数,2表示保留2位小数.
     *
     *  @return 説明 运算结果
     */
    public static String doMath(String str1,String str2,int type,int llen)
    {
        BigDecimal a = new BigDecimal(str1);
        BigDecimal b = new BigDecimal(str2);
        
        switch(type)
        {
            case 1:
                return a.add(b).setScale(llen,4).toString();
            case 2:
                return a.add(b.negate()).setScale(llen,4).toString();
            case 3:
                return a.multiply(b).setScale(llen,4).toString();
            case 4:
                return a.divide(b,llen,4).toString();
            default:
                throw new IllegalArgumentException();
                
        }
    }

    /**
     * 根据辅币位数得到相应的pattern
     * 
     * @param tempDecpos
     * @author xulc
     * @return
     */
    private static String getPattern(Integer tempDecpos)
    {
        int dec = 2;
        
        try
        {
            dec = tempDecpos.intValue();
        }
        catch(Exception e){}
        
        switch(dec)
        {
            case 0:
                return NO_DECPOS;
            case 1:
                return ONE_DECPOS;
            case 2:
                return TWO_DECPOS;
            case 3:
                return THR_DECPOS;
            default:
                return TWO_DECPOS;
        }
    }

    private static class MyComparator implements Comparator
    {
        private String curCdeKey;
        
        public MyComparator(String curCdeKey)
        {
            this.curCdeKey = curCdeKey;
        }
        
        public int compare(Object o1,Object o2)
        {
            Object tmp1 = ((Map)o1).get(curCdeKey);
            Object tmp2 = ((Map)o2).get(curCdeKey);
            int i1 = 0;
            int i2 = 0;
            
            if (tmp1 instanceof String)
            {
                i1 = Integer.parseInt((String)tmp1);
                i2 = Integer.parseInt((String)tmp2);
            }
            else
            {
                i1 = Integer.parseInt(((Currency)tmp1).getCode());
                i2 = Integer.parseInt(((Currency)tmp2).getCode());
            }
            
            if (i1 < i2)
                return 1;
            else
                return 0;
        }
    }
    public static void main(String atrs[]){
    	
    }

    /**
     * 根据牌价和金额计算出转换为人民币金额
     * 
     */
    public static BigDecimal exchangeRMBAmount(BigDecimal rate,BigDecimal amount){
		amount = (amount.multiply(rate)).divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP);
		
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();

		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		String strRet = df.format(amount);
		
		return new BigDecimal(strRet.replaceAll(",", ""));
    }
}