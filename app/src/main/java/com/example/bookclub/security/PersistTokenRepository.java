package com.example.bookclub.security;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PersistTokenRepository implements PersistentTokenRepository {
	@Override
	public void createNewToken(PersistentRememberMeToken token) {
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		return null;
	}

	@Override
	public void removeUserTokens(String username) {
	}
}
