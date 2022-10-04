package com.example.bookclub.common.mysqlconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {
	/**
	 * 현재 트랜잭션이 읽기 전용인지 아닌지를 반환한다.
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
		log.info("current dataSourceType is ReadOnly ? : {}", isReadOnly);
		return isReadOnly ? "read" : "write";
	}
}
