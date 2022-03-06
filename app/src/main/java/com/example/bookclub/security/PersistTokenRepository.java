package com.example.bookclub.security;

import com.example.bookclub.dto.RememberToken;
import com.example.bookclub.utils.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class PersistTokenRepository implements PersistentTokenRepository {
	private final StringRedisTemplate stringRedisTemplate;

	public PersistTokenRepository(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		RememberToken rememberToken = RememberToken.builder()
				.username(token.getUsername())
				.tokenValue(token.getTokenValue())
				.series(token.getSeries())
				.date(token.getDate())
				.build();

		stringRedisTemplate.opsForValue()
				.set(token.getSeries(), JsonUtil.toJson(rememberToken), 60 * 60 * 24 * 31, TimeUnit.DAYS);
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		String payload = stringRedisTemplate.opsForValue().get(series);

		try {
			RememberToken rememberToken = JsonUtil.fromJson(payload, RememberToken.class);
			rememberToken.setTokenValue(tokenValue);
			rememberToken.setDate(lastUsed);
			stringRedisTemplate.opsForValue()
					.set(series, JsonUtil.toJson(rememberToken), 60 * 60 * 24 * 31, TimeUnit.DAYS);
			log.debug("Remember me token is updated. series={}", series);
		} catch(Exception e) {
			log.error("Persistent token is not valid. payload={}, error={}", payload, e);
		}
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		String payload = stringRedisTemplate.opsForValue().get(seriesId);
		if (payload == null) return null;

		try {
			RememberToken rememberToken = JsonUtil.fromJson(payload, RememberToken.class);
			PersistentRememberMeToken token = new PersistentRememberMeToken(
					rememberToken.getUsername()
					,seriesId
					,rememberToken.getTokenValue()
					,rememberToken.getDate()
			);

			return token;
		} catch (Exception e) {
			log.error("Persistent token is not valid. payload={}, error={}", payload, e);
			return null;
		}
	}

	@Override
	public void removeUserTokens(String username) {
		stringRedisTemplate.opsForValue().getOperations().delete(username);
	}
}
