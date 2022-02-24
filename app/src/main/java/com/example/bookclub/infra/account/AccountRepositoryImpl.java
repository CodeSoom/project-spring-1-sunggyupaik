package com.example.bookclub.infra.account;

import com.example.bookclub.domain.Account;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.bookclub.domain.QAccount.account;
import static com.example.bookclub.domain.QStudy.study;
import static com.example.bookclub.domain.QUploadFile.uploadFile;

@Repository
public class AccountRepositoryImpl implements AccountRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public AccountRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.queryFactory = jpaQueryFactory;
	}

	@Override
	public Optional<Account> findById(Long id) {
		return Optional.ofNullable(
				queryFactory
						.select(account).distinct()
						.from(account)
						.leftJoin(account.study, study).fetchJoin()
						.leftJoin(account.uploadFile, uploadFile).fetchJoin()
						.where(account.id.eq(id))
						.fetchOne()
				);
	}

	@Override
	public Optional<Account> findByEmail(String email) {
		return Optional.ofNullable(
				queryFactory
						.select(account).distinct()
						.from(account)
						.leftJoin(account.study, study).fetchJoin()
						.leftJoin(account.uploadFile, uploadFile).fetchJoin()
						.where(account.email.eq(email))
						.fetchOne()
		);
	}
}
