package com.example.bookclub.common;

import com.example.bookclub.security.UserAccount;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 시큐리티에서 인증정보를 반환한다.
 */
public class AuditorAwareImpl implements AuditorAware<String> {
	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if(authentication == null || !authentication.isAuthenticated()
				|| authentication.getPrincipal().equals("anonymousUser")) {
			return Optional.empty();
		}

		return Optional.of(((UserAccount)authentication.getPrincipal()).getUsername());
	}
}
