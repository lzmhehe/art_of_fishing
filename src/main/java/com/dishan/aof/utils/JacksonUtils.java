package com.dishan.aof.utils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;


public class JacksonUtils {


    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        // 如果json中有新增的字段并且是实体类类中不存在的，不报错
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        // 如果存在未知属性，则忽略不报错
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许key没有双引号
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许key有单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 允许整数以0开头
        mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        // 允许字符串中存在回车换行控制符
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        // ignore null
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 对象转为json字符串
     *
     * @param obj 待转换对象
     * @return json格式字符串
     */
    public static String toJsonString(Object obj) {
        return toJsonString(obj, false);
    }

    /**
     * 对象转为json字符串
     *
     * @param obj    待转换对象
     * @param format 是否格式化
     * @return json格式字符串
     */
    public static String toJsonString(Object obj, boolean format) {
        if (Objects.isNull(obj)) {
            return null;
        }
        try {
            return format ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj) : mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonException(" Parse Object to String error ", e);
        }
    }

    /**
     * 转换json字符串为json对象
     *
     * @param text  待转换文本
     * @param clazz 转换为对象类型
     * @param <T>   换对象类型
     * @return 对象
     */
    public static <T> T parse(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text) || clazz == null) {
            return null;
        }
        if (clazz.equals(String.class)) {
            return (T) text;
        }
        try {
            return mapper.readValue(text, clazz);
        } catch (IOException e) {
            throw new JsonException(" Parse String to Object error ", e);
        }
    }

    /**
     * 转换json字符串为对象集合
     *
     * @param text  待转换文本
     * @param clazz 转换为对象类型
     * @param <T>   转换对象类型
     * @return 对象集合
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text) || clazz == null) {
            return Collections.emptyList();
        }
        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, clazz);
        try {
            return mapper.readValue(text, javaType);
        } catch (IOException e) {
            throw new JsonException(" Parse String to Array error ", e);
        }
    }

    /**
     * json异常
     */
    public static class JsonException extends RuntimeException {
        public JsonException() {
            super();
        }

        public JsonException(String message) {
            super(message);
        }

        public JsonException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

