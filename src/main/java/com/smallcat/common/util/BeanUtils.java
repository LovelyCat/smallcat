package com.smallcat.common.util;

import com.smallcat.common.bean.BaseResultVo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class BeanUtils {

    public static List<String> getClassFieldList(Class clazzClass) {
        List<String> fieldNameList = new ArrayList<String>();

        Field fieldList[] = clazzClass.getDeclaredFields();
        try {
            Field.setAccessible(fieldList, true);
            for (Field field : fieldList) {
                if(Modifier.isStatic(field.getModifiers())){
                    // 过滤掉静态属性
                    continue;
                }
                fieldNameList.add(field.getName());
            }
        } catch (Exception e) {
            fieldNameList = new ArrayList<String>();
        }
        return fieldNameList;
    }

    public static void main(String[] args) {
        for (String fieldName : getClassFieldList(BaseResultVo.class)) {
            System.out.println(fieldName);
        }
    }
}
