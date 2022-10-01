package com.example.bookclub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisCacheConfig {
	@Value("${spring.redis.cache.host}")
	private String hostName;

	@Value("${spring.redis.cache.port}")
	private int port;

	@Bean
	public RedisConnectionFactory redisCacheConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(hostName);
		redisStandaloneConfiguration.setPort(port);
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public RedisCacheManager redisCacheManager(RedisConnectionFactory redisCacheConnectionFactory) {
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
				.defaultCacheConfig()
				.disableCachingNullValues()
				.serializeValuesWith(
						RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.java()));

		Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
		redisCacheConfigurationMap.put("Interviews", redisCacheConfiguration.entryTtl(Duration.ofMinutes(5)));

		return RedisCacheManager.RedisCacheManagerBuilder
				.fromConnectionFactory(redisCacheConnectionFactory)
				.cacheDefaults(redisCacheConfiguration)
				.withInitialCacheConfigurations(redisCacheConfigurationMap)
				.build();
	}
}
