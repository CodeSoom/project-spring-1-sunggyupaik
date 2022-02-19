package com.example.bookclub.infra;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.Optional;

public class AccountRepositoryImpl implements AccountRepository {
	private final JPAQueryFactory queryFactory;

	public AccountRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.queryFactory = jpaQueryFactory;
	}

	@Override
	public Optional<Account> findById(Long id) {
		return Optional.empty();
	}

	@Override
	public Optional<Account> findByEmail(String email) {
		return Optional.empty();
	}
}
