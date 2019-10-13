package com.noon.shop.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    //类型转换
   public static <T> List<T> linkedHashMap2List(List<T> list,Class<T> targetClass){
       if ( list==null||list.size()==0){
           return null;
       }
       JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, targetClass);
       return objectMapper.convertValue(list,javaType);
    }
    //对象转字符串
    public static <T> String obj2String(T obj){
        if (obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    //字符串转对象
    public static <T> T string2Obj(String str,Class<T> clazz){
        if (StringUtils.isEmpty(str) || clazz == null){
            return null;
        }//new TypeReference<List<GetRigSmsResult>>() { }
        try {
            return clazz.equals(String.class)? (T) str :objectMapper.readValue(str,clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    //字符串转List
    public static <T> List<T> string2List(String jsonListStr, Class<T> targetClass) {
        List<T> result = null;
        try {

            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, targetClass);
            result = objectMapper.readValue(jsonListStr, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
//json转Map
    public static <K, V> Map<K, V> jsonToMap(String jsonMapStr, Class<K> keyClass, Class<V> valueClass) {
        Map<K, V> result;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JavaType javaType = mapper.getTypeFactory().constructParametricType(Map.class, keyClass, valueClass);
            result = mapper.readValue(jsonMapStr, javaType);
        } catch (IOException e) {
            throw new RuntimeException("objectToJson Class<T> valueType err:" + e.getMessage());
        }
        return result;
    }
}
