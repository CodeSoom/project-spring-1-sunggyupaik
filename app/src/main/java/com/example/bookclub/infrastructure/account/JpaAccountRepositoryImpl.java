package com.example.bookclub.infrastructure.account;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.favorite.QFavorite;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.bookclub.domain.account.QAccount.account;
import static com.example.bookclub.domain.study.QStudy.study;
import static com.example.bookclub.domain.uplodfile.QUploadFile.uploadFile;

@Repository
public class JpaAccountRepositoryImpl implements AccountRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public JpaAccountRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
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
						.leftJoin(account.favorites, QFavorite.favorite).fetchJoin()
						.leftJoin(account.study, study).fetchJoin()
						.leftJoin(account.uploadFile, uploadFile).fetchJoin()
						.where(account.email.eq(email))
						.fetchOne()
		);
	}

	@Override
	public long getAllAccountsCount() {
		return queryFactory
				.selectFrom(account)
				.fetchCount();
	}
}
