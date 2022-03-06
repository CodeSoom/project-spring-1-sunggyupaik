package com.example.bookclub.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;

@Log4j2
public class JsonUtil {
	private final ObjectMapper objectMapper;

	private JsonUtil() {
		objectMapper = new ObjectMapper();
	}

	public static JsonUtil getInstance() {
		return new JsonUtil();
	}

	private static ObjectMapper getMapper() {
		return getInstance().objectMapper;
	}

	public static String toJson(Object value) {
		try {
			return getMapper().writeValueAsString(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] toJsonAsByte(Object value) {
		try {
			return getMapper().writeValueAsBytes(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromJson(byte[] json, Class<T> cls) {
		try {
			return getMapper().readValue(json, cls);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromJson(String json, Class<T> cls) {
		try {
			return getMapper().readValue(json, cls);
		} catch (Exception e) {
			log.warn("Json parsing is failed. origin_json={}", json);
			throw new RuntimeException(e);
		}
	}
	public static <T> T fromJson(BufferedReader json, TypeReference<T> typeReference) {
		try {
			return getMapper().readValue(json, typeReference);
		} catch (Exception e) {
			log.warn("Json parsing is failed. origin_json={}", json);
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromJson(String json, TypeReference<T> typeReference) {
		try {
			return getMapper().readValue(json, typeReference);
		} catch (Exception e) {
			log.warn("Json parsing is failed. origin_json={}", json);
			throw new RuntimeException(e);
		}
	}

	public static String toPrettyJson(String value) {
		Object jsonObject = JsonUtil.fromJson(value, Object.class);
		try {
			return getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "";
		}
	}
}
