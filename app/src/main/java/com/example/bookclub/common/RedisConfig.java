package com.example.bookclub.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class RedisConfig {
	private final ObjectMapper objectMapper;

	public RedisConfig(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public RedisCacheManager redisCacheManager() {
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
				.defaultCacheConfig()
				.disableCachingNullValues()
				.serializeValuesWith(
						RedisSerializationContext.SerializationPair
								.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper))
				);

		Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
		redisCacheConfigurationMap.put("Studies", redisCacheConfiguration.entryTtl(Duration.ofMinutes(5)));
		return RedisCacheManager.RedisCacheManagerBuilder
				.fromConnectionFactory(redisConnectionFactory())
				.cacheDefaults(redisCacheConfiguration)
				.withInitialCacheConfigurations(redisCacheConfigurationMap)
				.build();
	}
}
