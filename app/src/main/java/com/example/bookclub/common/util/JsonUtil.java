package com.example.bookclub.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;

/**
 * json 문자열과 객체를 변환한다.
 */
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

	/**
	 * 주어진 객체을 문자열로 변환하고 반환한다.
	 *
	 * @param value 객체
	 * @return 변환된 문자열
	 */
	public static String toJson(Object value) {
		try {
			return getMapper().writeValueAsString(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 주어진 객체을 바이트 문자열로 변환하고 반환한다.
	 *
	 * @param value 객체
	 * @return 변환된 바이트 문자열
	 */
	public static byte[] toJsonAsByte(Object value) {
		try {
			return getMapper().writeValueAsBytes(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 주어진 바이트 문자열을 주어진 클래스명 형으로 변환하고 반환한다.
	 *
	 * @param json 바이트 문자열
	 * @param cls 클래스명
	 * @param <T> 형
	 * @return 변환된 클래스명 형
	 */
	public static <T> T fromJson(byte[] json, Class<T> cls) {
		try {
			return getMapper().readValue(json, cls);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 주어진 문자열을 주어진 클래스명 형으로 변환하고 반환한다.
	 *
	 * @param json 문자열
	 * @param cls 클래스명
	 * @param <T> 형
	 * @return 변환된 클래스명 형
	 */
	public static <T> T fromJson(String json, Class<T> cls) {
		try {
			return getMapper().readValue(json, cls);
		} catch (Exception e) {
			log.warn("Json parsing is failed. origin_json={}", json);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 주어진 버퍼 문자열을 주어진 타입명 형으로 변환하고 반환한다.
	 *
	 * @param json 버퍼 문자열
	 * @param typeReference 타입명
	 * @param <T> 형
	 * @return 변환된 타입명 형
	 */
	public static <T> T fromJson(BufferedReader json, TypeReference<T> typeReference) {
		try {
			return getMapper().readValue(json, typeReference);
		} catch (Exception e) {
			log.warn("Json parsing is failed. origin_json={}", json);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 주어진 문자열을 주어진 타입명 형으로 변환하고 반환한다.
	 *
	 * @param json 문자열
	 * @param typeReference 타입명
	 * @param <T> 형
	 * @return 변환된 타입명 형
	 */
	public static <T> T fromJson(String json, TypeReference<T> typeReference) {
		try {
			return getMapper().readValue(json, typeReference);
		} catch (Exception e) {
			log.warn("Json parsing is failed. origin_json={}", json);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 주어진 문자열을 가독성 좋은 json으로 변환하고 반환한다.
	 *
	 * @param value 문자열
	 * @return 가독성 좋은 json
	 */
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
