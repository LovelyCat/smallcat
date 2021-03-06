package com.smallcat.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;


/**
 * StringUtil.java 字符串处理工具类
 *
 * @author MIKM Written Date: 2006-3-31
 *         <p>
 *         Modified By: Modified Date:
 * @version 1.0
 */
public class StringUtil {

    private static Log log = LogFactory.getLog(StringUtil.class);

    /**
     * 去掉字符串首尾的空格 判断字符串是否为空，为空直接返回，不为空去掉空格后返回
     * <p>
     * Written Date: 2007-8-13
     *
     * @param str
     * @return
     * @author xulc
     */
    public static String trim(String str) {
        if (isEmpty(str))
            return str;
        return str.trim();
    }

    /**
     * 去掉字符串首尾的空格及全角空格 判断字符串是否为空，为空直接返回，不为空去掉空格后返回
     * <p>
     * Written Date: 2007-8-13
     *
     * @param str
     * @return
     * @author xulc
     */
    public static String trim2(String str) {
        if (isEmpty(str))
            return str;
        return trimFilling(trimFilling(str.trim(), '　', false), '　', true);
    }

    /**
     * 去除字串中的填充字符
     *
     * @param str           字串
     * @param filling       填充字符
     * @param leftJustified true：字串左对齐；false：字串右对齐
     * @return 去除填充字符后的字串
     */
    public static String trimFilling(String str, char filling, boolean leftJustified) {
        if (str == null || str.length() == 0)
            return str;
        if (leftJustified) {
            int pos = str.length() - 1;
            for (; pos >= 0; pos--) {
                if (str.charAt(pos) != filling)
                    break;
            }
            return str.substring(0, pos + 1);
        }
        int pos = 0;
        for (; pos < str.length(); pos++) {
            if (str.charAt(pos) != filling)
                break;
        }
        return str.substring(pos);
    }

    /**
     * 去除HTML字串中的控制字符及不可视字符
     *
     * @param str HTML字串
     * @return 返回的字串
     */
    public static String escapeHTML(String str) {
        int length = str.length();
        int newLength = length;
        boolean someCharacterEscaped = false;
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            int cint = 0xffff & c;
            if (cint < 32)
                switch (c) {
                    case 11:
                    default:
                        newLength--;
                        someCharacterEscaped = true;
                        break;

                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                        break;
                }
            else
                switch (c) {
                    case '"':
                        newLength += 5;
                        someCharacterEscaped = true;
                        break;

                    case '&':
                    case '\'':
                        newLength += 4;
                        someCharacterEscaped = true;
                        break;

                    case '<':
                    case '>':
                        newLength += 3;
                        someCharacterEscaped = true;
                        break;
                }
        }
        if (!someCharacterEscaped)
            return str;

        StringBuffer sb = new StringBuffer(newLength);
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            int cint = 0xffff & c;
            if (cint < 32)
                switch (c) {
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                        sb.append(c);
                        break;
                }
            else
                switch (c) {
                    case '"':
                        sb.append("&quot;");
                        break;

                    case '\'':
                        sb.append("&apos;");
                        break;

                    case '&':
                        sb.append("&amp;");
                        break;

                    case '<':
                        sb.append("&lt;");
                        break;

                    case '>':
                        sb.append("&gt;");
                        break;

                    default:
                        sb.append(c);
                        break;
                }
        }
        return sb.toString();
    }

    /**
     * 去除SQL字串中的控制字符
     *
     * @param str SQL字串
     * @return 返回的字串
     */
    public static String escapeSQL(String str) {
        int length = str.length();
        int newLength = length;
        for (int i = 0; i < length; ) {
            char c = str.charAt(i);
            switch (c) {
                case 0:
                case '"':
                case '\'':
                case '\\':
                    newLength++;
                default:
                    i++;
                    break;
            }
        }
        if (length == newLength)
            return str;

        StringBuffer sb = new StringBuffer(newLength);
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;

                case '"':
                    sb.append("\\\"");
                    break;

                case '\'':
                    sb.append("\\'");
                    break;

                case 0:
                    sb.append("\\0");
                    break;

                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * 将一个字符串分解成几个段
     *
     * @param str    字符串
     * @param segLen 每段限长
     * @param segNum 分解段数
     * @return 分解后的字串组
     */
    public static String[] split(String str, int segLen, int segNum) {
        String[] result = new String[segNum];
        if (str == null || str.length() == 0)
            return result;

        byte[] strByte;
        try {
            strByte = str.getBytes("GBK");
        } catch (UnsupportedEncodingException ex) {
            strByte = str.getBytes();
        }
        int pos = 0;
        for (int i = 0; i < segNum; i++) {
            int actLen = ((strByte.length - pos) < segLen) ? (strByte.length - pos) : segLen;
            byte[] b = new byte[actLen];
            System.arraycopy(strByte, pos, b, 0, actLen);
            result[i] = new String(b);
            pos += actLen;
            if (pos >= strByte.length)
                break;
        }
        return result;
    }

    /**
     * 将一个字符串分解成几个段，每段都能正常转换为IBM935字符集
     *
     * @param str    字符串
     * @param segLen 每段限长
     * @param segNum 分解段数
     * @return 分解后的字串组
     */
    public static String[] split4Cp935(String str, int segLen, int segNum) {

        String[] result = new String[segNum];

        byte[] strByte;
        try {
            strByte = str.getBytes("GBK");
        } catch (UnsupportedEncodingException ex) {
            strByte = str.getBytes();
        }
        int strLen = strByte.length;
        byte[] tmpByte = new byte[2 * strLen + segLen];

        int head;
        int flag;
        int count;

        int strCount = 0;
        int segCount = 0;
        int lastStrCount = 0;

        for (flag = 0, head = 0, count = 0; strCount < strLen && segCount < segNum; count++) {
            if ((strByte[strCount] & 0x80) != 0) {
                head = head == 1 ? 0 : 1;
                if (flag == 0) {
                    tmpByte[count] = (byte) ' ';
                    flag = 1;
                    count++;
                }

                if ((count == ((count / segLen) + 1) * segLen - 2) && (head == 1)) {
                    tmpByte[count] = (byte) ' ';
                    tmpByte[count + 1] = (byte) ' ';
                    result[segCount] = str.substring(lastStrCount, strCount);
                    lastStrCount = strCount;
                    segCount++;
                    tmpByte[count + 2] = (byte) ' ';
                    flag = 1;
                    count = count + 3;
                } else if (((segLen * ((count + 1) / segLen) - 1) == count) && (head == 1)) {
                    tmpByte[count] = (byte) ' ';
                    byte[] tmp = new byte[strCount - lastStrCount];
                    System.arraycopy(strByte, lastStrCount, tmp, 0, tmp.length);
                    result[segCount] = new String(tmp);
                    lastStrCount = strCount;
                    segCount++;
                    tmpByte[count + 1] = (byte) ' ';
                    count = count + 2;
                    flag = 1;
                } else if (((segLen * (count / segLen)) == count) && (head == 1)) {
                    byte[] tmp = new byte[strCount - lastStrCount];
                    System.arraycopy(strByte, lastStrCount, tmp, 0, tmp.length);
                    result[segCount] = new String(tmp);
                    lastStrCount = strCount;
                    segCount++;
                    tmpByte[count] = (byte) ' ';
                    count++;
                    flag = 1;
                }
            } else {
                if (flag == 1) {
                    tmpByte[count] = (byte) ' ';
                    count++;
                    flag = 0;
                    if (count == (count / segLen) * segLen) {
                        byte[] tmp = new byte[strCount - lastStrCount];
                        System.arraycopy(strByte, lastStrCount, tmp, 0, tmp.length);
                        result[segCount] = new String(tmp);
                        lastStrCount = strCount;
                        segCount++;
                    } else {
                        if ((segLen * ((count + 1) / segLen) - 1) == count) {
                            byte[] tmp = new byte[strCount - lastStrCount + 1];
                            System.arraycopy(strByte, lastStrCount, tmp, 0, tmp.length);
                            result[segCount] = new String(tmp);
                            lastStrCount = strCount + 1;
                            segCount++;
                        }
                    }
                } else {
                    if ((segLen * ((count + 1) / segLen) - 1) == count) {
                        byte[] tmp = new byte[strCount - lastStrCount + 1];
                        System.arraycopy(strByte, lastStrCount, tmp, 0, tmp.length);
                        result[segCount] = new String(tmp);
                        lastStrCount = strCount + 1;
                        segCount++;
                    }
                }

            }
            tmpByte[count] = strByte[strCount];
            strCount++;
            if (strCount >= strLen && segCount < segNum) {
                byte[] tmp = new byte[strCount - lastStrCount];
                System.arraycopy(strByte, lastStrCount, tmp, 0, tmp.length);
                result[segCount] = new String(tmp);
                lastStrCount = strCount;
                segCount++;
            }

            if ((count + 1) % segLen == 0) {
                flag = 0;
            }
        }

        return result;
    }

    /**
     * 取得字符串字节长度,使用缺省字符集
     *
     * @param str
     * @return length
     */
    public static int getByteLength(String str) {
        return str.getBytes().length;
    }

    /**
     * 截取最长为length字节的字符串
     *
     * @param str
     * @param length
     * @return str
     */
    public static String subString(String str, String encode, int length) {
        if (str == null || length < 0)
            return null;
        int zhl = 0, enl = 0;
        if (zhLen.containsKey(encode)) {
            zhl = ((Integer) zhLen.get(encode)).intValue();
            enl = ((Integer) enLen.get(encode)).intValue();
        } else {
            try {
                zhl = zh.getBytes(encode).length;
            } catch (UnsupportedEncodingException e) {
                zhl = zh.getBytes().length;
            }
            try {
                enl = en.getBytes(encode).length;
            } catch (UnsupportedEncodingException e) {
                enl = en.getBytes().length;
            }
            zhLen.put(encode, new Integer(zhl));
            enLen.put(encode, new Integer(enl));
        }

        int len = 0;
        int strlen = str.length();
        int i;
        for (i = 0; i < strlen; i++) {
            if (str.charAt(i) < 0x80)
                len += enl;
            else
                len += zhl;
            if (len > length)
                break;

        }

        return str.substring(0, i);
    }

    /**
     * 将带分隔符的字符串转换成list
     */
    public static List<String> splitString(String str, String separator) {
        return new ArrayList<String>(Arrays.asList(str.split(separator)));
    }

    public static String xml2String(byte[] xml) {
        if (xml == null)
            return null;
        if (xml.length == 0)
            return "";
        try {
            if (xml.length > 20) {
                int max = Math.min(100, xml.length);
                int i = 0;
                while (i < max && xml[i] != '<')
                    i++;
                int j = i;
                while (j < max && xml[j] != '>')
                    j++;
                if (j - i > 15) {
                    String head = new String(xml, i, j - i + 1, "ISO-8859-1");
                    i = head.indexOf("encoding=");
                    if (i != -1) {
                        i += "encoding=".length();
                        char q = head.charAt(i);
                        j = head.indexOf(q, i + 1);
                        if (j != -1) {
                            head = head.substring(i + 1, j);
                            return new String(xml, head);
                        }
                    }
                }
            }
        } catch (Throwable t) {
        }
        return new String(xml);
    }

    /**
     * 得到以head开头,tail结尾的子串
     *
     * @param buffer
     * @param head
     * @param tail
     * @return String
     */
    public static String subString(String buffer, String head, String tail) {
        if (buffer == null || head == null || tail == null)
            return buffer;
        int startNum = buffer.indexOf(head);
        int endNum = buffer.lastIndexOf(tail);

        startNum = startNum >= 0 ? startNum : 0;
        endNum = endNum >= 0 ? endNum + tail.length() : buffer.length();
        return buffer.substring(startNum, endNum);
    }

    /**
     * 判断对象或对象数组中每一个对象是否为空: 对象为null，字符序列长度为0，集合类、Map为empty
     *
     * @param object
     * @return
     */
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof CharSequence) {
            return ((CharSequence) object).length() == 0;
        }
        if (object instanceof Collection)
            return ((Collection<?>) object).isEmpty();

        if (object instanceof Map)
            return ((Map<?, ?>) object).isEmpty();

        if (object instanceof Object[]) {
            Object[] objectList = (Object[]) object;
            boolean empty = true;
            for (int i = 0; i < objectList.length; i++) {
                if (!isEmpty(objectList[i])) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        return false;
    }

    /**
     * 将用separator分隔的String转化为List，如果str中没有separator则返回的List中只有 str一项
     *
     * @param str
     * @param separator
     * @return
     */
    public static List<String> str2List(String str, String separator) {
        List<String> result = new ArrayList<String>();
        if (str.indexOf(separator) < 0) {
            result.add(str);
        } else {
            String[] strArray = str.split(separator);
            for (int i = 0; i < strArray.length; i++) {
                result.add(strArray[i]);
            }
        }
        return result;
    }

    /**
     * 去除字符串中空白字符
     *
     * @param str
     * @return
     */
    public static String filterSBCCase(String str) {
        char[] ch = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ch.length; i++) {
            if (!Character.isWhitespace(ch[i])) {
                sb.append(String.valueOf(ch[i]));
            }
        }
        return sb.toString();
    }

    /**
     * 字符串是否全数字（无符号、小数点）
     *
     * @param str
     * @return
     */
    public static boolean isDigit(String str) {
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c < '0' || c > '9')
                return false;
        }
        return true;
    }

    /**
     * 字符串是否全数字或英文字母（无符号、小数点）
     *
     * @param str
     * @return
     */
    public static boolean isAlphaDigit(String str) {
        char character;
        for (int i = 0; i < str.length(); i++) {
            character = str.charAt(i);
            if ((character < '0' || character > '9')
                    && (character < 'A' || character > 'Z')
                    && (character < 'a' || character > 'z')) {
                return false;
            }
        }
        return true;
    }

    /**
     * get substring in bytes length
     *
     * @param orgString     original string
     * @param lengthInBytes bytes length
     * @return substring
     */
    public static final String subStringInBytes(String orgString, int startPos, int lengthInBytes) {

        if (orgString == null)
            return null;

        byte[] orgBytes;
        try {
            orgBytes = orgString.getBytes("GBK");
        } catch (UnsupportedEncodingException ex) {
            orgBytes = orgString.getBytes();
        }
        if (startPos < 0 || startPos > orgBytes.length)
            return null;
        else if (lengthInBytes < startPos)
            return null;

        byte[] newBytes;
        int newLength = orgBytes.length - startPos;
        if (lengthInBytes < newLength)
            newLength = lengthInBytes;

        newBytes = new byte[newLength];
        System.arraycopy(orgBytes, startPos, newBytes, 0, newLength);

        return new String(newBytes);
    }

    /**
     * 按千位分割格式格式化数字
     *
     * @param v
     * @param scale
     * @return
     */
    public static String parseStringPattern(Object v, int scale) {
        String temp = "###,###,###,###,###,###,###,##0";
        if (scale > 0)
            temp += ".";
        for (int i = 0; i < scale; i++)
            temp += "0";
        DecimalFormat format = new DecimalFormat(temp);
        return format.format(v).toString();
    }

    /**
     * 转为String[]
     *
     * @param object
     * @return
     */
    public static String[] getStringArray(Object object) {
        if (object instanceof String[])
            return (String[]) object;
        if (object instanceof String) {
            String tmpStrs[] = new String[1];
            tmpStrs[0] = (String) object;
            return tmpStrs;
        }
        return null;
    }

    /**
     * 对象的字符串表示
     *
     * @param obj
     * @return
     */
    public static final String objectToString(Object obj) {
        if (obj == null) {
            return "null";
        }
        StringBuffer buf = new StringBuffer();

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescs = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescs.length; i++) {
                String name = propertyDescs[i].getName();
                Method getter = propertyDescs[i].getReadMethod();
                if (getter != null && !name.equals("class")) {
                    Object value = getter.invoke(obj, new Object[]{null});
                    buf.append(name).append("=").append(value).append(" ");
                }
            }
        } catch (IllegalArgumentException e) {

        } catch (IntrospectionException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }

    public static String ADEncoding(String str) {
        if (str == null || str.length() == 0 || isDigit(str) && !str.startsWith("99"))
            return str;
        StringBuffer buf = new StringBuffer("99");
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c > '\u00ff')
                buf.append("99");
            else if (c > '~')
                buf.append("98");
            else if (c == '\t')
                buf.append("95");
            else if (c == '\r' || c == '\n')
                buf.append("96");
            else if (c < ' ')
                buf.append("97");
            else if (c >= '0' && c <= '9')
                buf.append('0').append(c);
            else if (c >= 'A' && c <= 'Z')
                buf.append((char) ((c - 'A' + 10) / 10 + '0')).append((char) ((c - 'A' + 10) % 10 + '0'));
            else if (c >= 'a' && c <= 'z')
                buf.append((char) ((c - 'a' + 40) / 10 + '0')).append((char) ((c - 'a' + 40) % 10 + '0'));
            else if (c < '0')
                buf.append((char) ((c - ' ' + 66) / 10 + '0')).append((char) ((c - ' ' + 66) % 10 + '0'));
            else if (c < 'A')
                buf.append((char) ((c - ':' + 82) / 10 + '0')).append((char) ((c - ':' + 82) % 10 + '0'));
            else if (c < 'a')
                buf.append((char) ((c - '[' + 89) / 10 + '0')).append((char) ((c - '[' + 89) % 10 + '0'));
            else
                // if (c >= '{')
                buf.append((char) ((c - '{' + 36) / 10 + '0')).append((char) ((c - '{' + 36) % 10 + '0'));
        }
        return buf.toString();
    }

    public static String ADDecoding(String str) {
        if (str == null || str.length() < 4 || !str.startsWith("99") || (str.length() & 1) == 1)
            return str;
        StringBuffer buf = new StringBuffer();
        for (int i = 2; i < str.length(); i += 2) {
            int n = (str.charAt(i) - '0') * 10 + str.charAt(i + 1) - '0';
            if (n < 10)
                buf.append(n);
            else if (n < 36)
                buf.append((char) (n - 10 + 'A'));
            else if (n < 40)
                buf.append((char) (n - 36 + '{'));
            else if (n < 66)
                buf.append((char) (n - 40 + 'a'));
            else if (n < 82)
                buf.append((char) (n - 66 + ' '));
            else if (n < 89)
                buf.append((char) (n - 82 + ':'));
            else if (n < 95)
                buf.append((char) (n - 89 + '['));
            else if (n == 95)
                buf.append('\t');
            else if (n == 96)
                buf.append('\n');
            else if (n == 97)
                buf.append('\u00a9');
            else if (n == 98)
                buf.append('\u00c5');
            else
                // if (n == 99)
                buf.append('\u2592');
        }
        return buf.toString();
    }

    private static Map<String, Integer> zhLen = new HashMap<String, Integer>();
    private static Map<String, Integer> enLen = new HashMap<String, Integer>();

    private static final String zh = "汉";

    private static final String en = "A";

    /**
     * 将List中内的MAP字串转换为MAP对象
     *
     * @param listMapInner
     * @return
     * @author caoke
     */
    public static List<Map<String, String>> listString2Map(List<String> listMapInner) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (int i = 0; i < listMapInner.size(); i++) {
            Object map = listMapInner.get(i);
            if (map != null) {
                list.add(string2Map(map.toString()));
            }
        }
        return list;
    }

    /**
     * 将MAP对象转换为字串
     *
     * @param map
     * @return
     * @author caoke
     */
    public static String map2String(Map<String, Object> map) {
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;
        for (Iterator<Entry<String, Object>> it = map.entrySet().iterator(); it.hasNext(); ) {
            Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key != null && value != null) {
                if (!isFirst) {
                    sb.append(",");
                } else {
                    isFirst = false;
                }
                sb.append(normalizeMapString(key.trim()));
                sb.append("=");
                sb.append(normalizeMapString(value.toString().trim()));
            }
        }
        return sb.toString();
    }

    /**
     * 将字串对象转换为MAP
     *
     * @param str
     * @return
     * @author caoke
     */
    public static Map<String, String> string2Map(String str) {
        Map<String, String> map = new HashMap<String, String>();
        String[] entrys = StringUtils.tokenizeToStringArray(str, ",");
        for (int i = 0; i < entrys.length; i++) {
            String[] entry = StringUtils.tokenizeToStringArray(entrys[i], "=");
            if (entry.length == 2) {
                map.put(specializeMapString(entry[0]), specializeMapString(entry[1]));
            }
        }
        return map;
    }

    private static final String[] ESC_TABLE = new String[]{"&cma&", "&equ&"};

    private static final String[] TALBLE = new String[]{",", "="};

    private static String normalizeMapString(String str) {
        if (str == null) {
            return null;
        }
        String result = str;
        for (int i = 0; i < TALBLE.length; i++) {
            result = StringUtils.replace(result, TALBLE[i], ESC_TABLE[i]);
        }
        return result;
    }

    private static String specializeMapString(String str) {
        if (str == null) {
            return null;
        }
        String result = str;
        for (int i = 0; i < TALBLE.length; i++) {
            result = StringUtils.replace(result, ESC_TABLE[i], TALBLE[i]);
        }
        return result;
    }

    /**
     * 取得类的简单名
     *
     * @param obj
     * @return 类的简单名
     */
    public static String getSimpleName(Object obj) {
        if (obj == null)
            return null;
        String name = obj.getClass().getName();
        if (name.toLowerCase().indexOf("$proxy") >= 0) {
            name = obj.toString();
            int idx = name.lastIndexOf("@");
            if (idx > 0)
                name = name.substring(0, idx);
        }
        return name.substring(name.lastIndexOf(".") + 1);
    }

    /**
     * 截字串
     *
     * @param str
     * @param length
     * @return
     */
    public static String subString(String str, int length) {
        if (str == null) {
            return "";
        }
        int end = str.length();
        if (length > end) {
            end = length;
        }
        return str.substring(0, end);
    }

    /**
     * 获取字符串的长度, 如果为null则长度为0
     *
     * @param str
     * @return
     */
    public static int length(String str) {
        if (isEmpty(str))
            return 0;
        else
            return str.trim().length();

    }

    /**
     * 海外对公交易查询汇总公用方法 将总金额按照币种分类，组成字符串
     *
     * @param list
     * @return String
     */
    public static String ListToString(List<Map<String, String>> list) {
        String listString = "";
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                String curr = list.get(i).get("currency");
                String amout = list.get(i).get("amount");
                listString = listString + curr + "|" + amout + "@";
            }
            return listString;
        } else {
            return null;
        }
    }

    public static boolean isBase64(byte[] data) {
        char[] chars = new String(data).toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= '0' && chars[i] <= '9' || chars[i] >= 'A' && chars[i] <= 'Z' || chars[i] >= 'a'
                    && chars[i] <= 'z' || chars[i] == '+' || chars[i] == '/' || chars[i] == '=') {
            } else
                return false;
        }
        return true;
    }

    /**
     * <p>
     * 方法shortClassName()。
     * </p>
     *
     * @param obj
     * @return
     * @author manbaum
     * @since Aug 20, 2008
     */
    public static String shortClassName(Object obj) {
        if (obj == null) {
            return "null";
        }

        Class<?> klass = obj instanceof Class ? (Class<?>) obj : obj.getClass();
        String fullname = klass.getName();
        int packageNameLength = klass.getPackage().getName().length();
        return packageNameLength > 0 ? fullname.substring(packageNameLength + 1) : fullname;
    }

    public static String CRLF = System.getProperty("line.separator");

    public static String ellipsisText(String longText, int maxLength) {
        if (longText == null)
            return "null";
        int index = longText.indexOf(CRLF);
        String firstLine = index >= 0 ? longText.substring(0, index) : longText;
        return firstLine.length() > maxLength ? firstLine.substring(0, maxLength) + "..." : firstLine;
    }

    public static String stringOf(Exception ex) {
        if (ex != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("Class: " + shortClassName(ex));
            sb.append("Message: " + ellipsisText(ex.getMessage(), 32));
            return sb.toString();
        } else {
            return "null";
        }
    }

    public static String stringOf(Date date) {
        return stringOf(TimeZone.getDefault(), date);
    }

    public static String stringOf(TimeZone timeZone, Date date) {
        if (date == null) {
            return "null";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        if (timeZone != null) {
            formatter.setTimeZone(timeZone);
        }
        return formatter.format(date);
    }

    /**
     * 返回srcStr字符串的前truncByteLen字节长度的子串。<br>
     * <p>
     * 如果截取的末字节是半个汉字，则略去该字节，要求返回完整的汉字。<br>
     * <p>
     * (1)srcStr == null || truncByteLen < 0 返回""<br>
     * (2)truncByteLen > srcStr的实际字节长度，则原样返回srcStr
     *
     * @param srcStr       待截取字符串
     * @param encode       编码方式
     * @param truncByteLen 截取字节数
     * @return 截取后的字符串
     * <p>
     * e.g. srcStr="ab你好", truncByteLen=3, 返回"ab"; truncByteLen=4, 返回"ab你"
     * <p>
     * Written Date: 2008-9－17
     * @author chenyx
     */
    public static String truncateStr(String srcStr, String encode, int truncByteLen) {
        if (srcStr == null || truncByteLen < 0)
            return "";

        int strLen = srcStr.length();
        String tempStr = "";

        try {
            if (truncByteLen <= strLen)
                tempStr = srcStr.substring(0, truncByteLen);// truncByteLen=0,则返回""
            else
                tempStr = srcStr;
        } catch (IndexOutOfBoundsException iobe) {
            return "";
        }

        int tempByteLen;
        try {
            tempByteLen = tempStr.getBytes(encode).length;
        } catch (UnsupportedEncodingException e) {
            tempByteLen = tempStr.getBytes().length;
        }

        while (tempByteLen > truncByteLen) {
            tempStr = tempStr.substring(0, tempStr.length() - 1);
            try {
                tempByteLen = tempStr.getBytes(encode).length;
            } catch (UnsupportedEncodingException e) {
                tempByteLen = tempStr.getBytes().length;
            }
        }
        return tempStr;
    }

    public static Locale toLocale(String localeStr) {
        if (localeStr == null)
            return null;
        String[] part = localeStr.split("_");
        if (part.length == 1)
            return new Locale(part[0].toLowerCase());
        if (part.length == 2)
            return new Locale(part[0].toLowerCase(), part[1].toUpperCase());
        if (part.length == 3)
            return new Locale(part[0].toLowerCase(), part[1].toUpperCase(), part[2].toUpperCase());
        return new Locale(localeStr);
    }

    /**
     * 服务记录的数据项组织成字符串
     *
     * @param transstatus 交易状态
     * @param currency    货币码
     * @param summary     简要说明
     * @param amount      交易金额
     * @param args        简要说明里的参数串
     * @return
     * @author
     */
    public static String creatContent(String transstatus, String currency, String summary, BigDecimal amount, String args) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("TransStatus", transstatus);
        map.put("CurrencyCode", currency);
        map.put("Summary", summary);
        map.put("Amount", amount);
        map.put("Args", fillNullArgs(args));
        return map2String(map);
    }

    /**
     * 使用“,”对原有描述进行分隔，对所有为空的内容替换为“-”，重新组合后返回
     * <p>
     * 特殊注意：当传入值为null时，返回”“
     * 当传入值为”“时，返回”-“
     * 当传入值只包含”,“时，返回”-,-“
     *
     * @param args
     * @return fillStr
     */
    public static String fillNullArgs(String args) {

        String fillStr = "";

        //参数为空指针返回空串
        if (args == null) {
            return fillStr;
        }

        //无限次匹配，保留末尾空数组
        String[] strArgs = args.split(",", -1);

        //替换空元素
        for (int i = 0; i < strArgs.length; i++) {
            if (StringUtil.isEmpty(strArgs[i]) || "null".equals(strArgs[i])) {
                strArgs[i] = "-";
            }
            fillStr = fillStr + strArgs[i] + ",";
        }

        //去掉最后的”,“
        if (!StringUtil.isEmpty(fillStr) && (fillStr.substring(fillStr.length() - 1, fillStr.length()).equals(","))) {
            fillStr = fillStr.substring(0, fillStr.length() - 1);
        }

        return fillStr;
    }


    /**
     * 验证时间（小时分钟）是否合法  0000-2359
     *
     * @param time
     * @return
     */
    public static boolean checkTime(String time) {
        boolean flag = true;

        if (null == time || "".equals(time))
            flag = false;
        /**必须为4位数字**/
        if (4 != time.length())
            flag = false;
        /**必须是数字**/
        if (!isDigit(time))
            flag = false;

        if (flag) {
            int num1 = Integer.parseInt(time.substring(0, 2));
            int num2 = Integer.parseInt(time.substring(2, 4));
            if (num1 > 23) flag = false;
            if (num2 > 59) flag = false;
        }


        return flag;

    }

    /**
     * string.getBytes("ISO8859_1"), "UTF-8")
     *
     * @param string
     * @return
     */
    public static String iso2utf8(String string) {
        try {
            if (string != null) {
                string = new String(string.getBytes("ISO8859_1"), "UTF-8");
            }
        } catch (Exception e) {
            return string;
        }
        return string;
    }

    /**
     * 检查字符串在指定编码格式中的字节长度是否超出指定值
     * 主要用于检查中文是否超长
     *
     * @param str  检查的字符串
     * @param code 指定字符串编码(UTF-8,GBK等)
     * @param len  检查的长度
     * @return 如果匹配返回true, 否则返回false
     */
    public static boolean checkStrLenByCode(String str, String code, int len) {
        try {
            if (!StringUtil.isEmpty(str) && str.getBytes(code).length > len) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 对象赋值 （obj_s/obj_t）
     *
     * @param obj_s 源对象
     * @param obj_t 目标对象
     * @return
     */
    public static void ObjectValueCopy(Object obj_s, Object obj_t) throws Exception {

        try {
            /** 获取源对象类及其属性 */
            Class<?> type = obj_s.getClass();
            Field[] fields = type.getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {

                /** 获取其属性名称，将首字母大写 */
                String fieldName = fields[i].getName();
                String stringLetter = fieldName.substring(0, 1).toUpperCase();
                /** 获得相应属性的getXXX和setXXX方法名称 */
                String getName = "get" + stringLetter + fieldName.substring(1);
                String setName = "set" + stringLetter + fieldName.substring(1);
                /** boolean类型的处理 */
                if (fields[i].getType().toString().equals("boolean"))
                    getName = "is" + stringLetter + fieldName.substring(1);
                /** 获取相应的方法 */
                Method getMethod = type.getMethod(getName, new Class[]{});
                Method setMethod = type.getMethod(setName, new Class[]{fields[i].getType()});
                /** 调用源对象的getXXX（）方法 */
                Object value = getMethod.invoke(obj_s, new Object[]{});
                /** 调用拷贝对象的setXXX（）方法 */
                setMethod.invoke(obj_t, new Object[]{value});

            }
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * 返回对象所转化字符串并去首尾空格，如果为空或去首位空格后为空则返回空字符串
     *
     * @param obj
     * @return
     */
    public static String getEmptyOrString(Object obj) {
        if (isEmpty(obj) || isEmpty(obj.toString().trim())) {
            return "";
        } else {
            return obj.toString().trim();
        }
    }

    /**
     * 返回对象所转化字符串并去首尾空格，如果为空或去首位空格后为空则返回默认字符串
     *
     * @param obj
     * @param def
     * @return
     */
    public static String getDefaultOrString(Object obj, String def) {
        if (isEmpty(obj) || isEmpty(obj.toString().trim())) {
            return def;
        } else {
            return obj.toString().trim();
        }
    }


    /**
     * 根据固定长度格式化字符串
     *
     * @param str          原始字符串
     * @param formatLength 要求的的长度
     * @param chr          用什么字符补齐
     * @param lpar         左补齐还是右补齐
     * @return String
     */
    public static String formatStr(String str, int formatLength, String chr, boolean lpar) {
        if (str == null)
            str = "";
        try {
            byte bytechr[] = chr.getBytes();
            if (str.getBytes("gbk").length < formatLength) {

                int length = formatLength - str.getBytes("gbk").length;

                for (int i = 0; i < length; i++) {
                    if (lpar)// 左补齐
                        str = chr + str;
                    else
                        str = str + chr;
                }

            } else {
                int count = 0;
                byte[] tempStr = str.getBytes("GBK");
                byte[] fixStr = new byte[formatLength];
                for (int i = 0; i <= fixStr.length - 1; i++) {
                    if (!lpar)
                        fixStr[i] = tempStr[i];
                    else
                        fixStr[i] = tempStr[tempStr.length - formatLength + i];
                    if (fixStr[i] < 0)
                        count++;
                }
                if (count % 2 != 0 && !lpar)
                    fixStr[formatLength - 1] = bytechr[0];
                if (count % 2 != 0 && lpar)
                    fixStr[0] = bytechr[0];
                str = new String(fixStr, "GBK");
            }
            return str;
            // return lpar?StringUtil.subStringInBytes(str,
            // str.getBytes("gbk").length-formatLength,
            // formatLength):StringUtil.subStringInBytes(str, 0, formatLength);
        } catch (UnsupportedEncodingException ex) {
            log.error(ex, ex);
            return formatStr("", formatLength, " ", lpar);
        }
        // return
        // lpar?str.substring(str.length()-formatLength):str.substring(0,formatLength);

    }

    /**
     * Returns a formatted string using the specified format string and
     * arguments.
     * <p>
     * <p> The locale always used is the one returned by {@link
     * java.util.Locale#getDefault() Locale.getDefault()}.
     *
     * @param format A <a href="../util/Formatter.html#syntax">format string</a>
     * @param args   Arguments referenced by the format specifiers in the format
     *               string.  If there are more arguments than format specifiers, the
     *               extra arguments are ignored.  The number of arguments is
     *               variable and may be zero.  The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by
     *               the <a href="http://java.sun.com/docs/books/vmspec/">Java
     *               Virtual Machine Specification</a>.  The behaviour on a
     *               <tt>null</tt> argument depends on the <a
     *               href="../util/Formatter.html#syntax">conversion</a>.
     * @return A formatted string
     * @throws IllegalFormatException If a format string contains an illegal syntax, a format
     *                                specifier that is incompatible with the given arguments,
     *                                insufficient arguments given the format string, or other
     *                                illegal conditions.  For specification of all possible
     *                                formatting errors, see the <a
     *                                href="../util/Formatter.html#detail">Details</a> section of the
     *                                formatter class specification.
     * @throws NullPointerException   If the <tt>format</tt> is <tt>null</tt>
     * @see java.util.Formatter
     * @since 1.5
     */
    public static String format(String format, Object... args) {
        Formatter formatter = new Formatter();
        String rtnVal = formatter.format(format, args).toString();
        formatter.close();
        return rtnVal;
    }

    public static boolean belong(String str, String[] aimStrings) {
        for (String aim : aimStrings)
            if (aim.equals(str))
                return true;
        return false;
    }

    /**
     * 将tokenId显示为中间隐藏
     *
     * @param tokenId
     * @return
     */
    public static String getDisplayTokenId(String tokenId) {
        if (tokenId == null || tokenId.length() == 0) {
            return tokenId;
        }

        StringBuffer sb = new StringBuffer(tokenId);
        StringBuffer token = new StringBuffer();
        if (tokenId.length() <= 10) {
            token.append(sb.substring(0, 3));
            token.append("***");
            token.append(sb.substring(sb.length() - 3, sb.length()));
        } else if (tokenId.length() > 10) {
            token.append(sb.substring(0, 4));
            token.append("*****");
            token.append(sb.substring(sb.length() - 4, sb.length()));
        }
        return token.toString();
    }

    /**
     * 将账户号码转换为页面显示的字符。（只显示前4位、后4位，其他显示"*"）
     *
     * @param accountNumber
     * @return
     */
    public static String getDisplayAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() == 0)
            return accountNumber;

        StringBuffer sb = new StringBuffer(accountNumber);
        int start = 4;
        int end = sb.length() - 4;
        if (start < sb.length() && start < end) {
            for (int i = start; i < end; i++) {
                sb.replace(i, i + 1, "*");
            }
        }
        return sb.toString();
    }


}
