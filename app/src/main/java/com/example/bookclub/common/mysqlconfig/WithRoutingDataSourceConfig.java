package com.example.bookclub.common.mysqlconfig;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 읽기, 쓰기 저장소를 설정하고 경우에 따라 라우팅되도록 한다
 */
@Configuration
@Slf4j
public class WithRoutingDataSourceConfig {
	/**
	 * 주어진 사용자 식별자와 사용자 이메일로 인코딩 된 토큰을 생성하고 반환한다.
	 *
	 * @return 쓰기 저장소
	 */
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.hikari.write")
	public DataSource writeDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	/**
	 * 주어진 사용자 식별자와 사용자 이메일로 인코딩 된 토큰을 생성하고 반환한다.
	 *
	 * @return 읽기 저장소
	 */
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari.read")
	public DataSource readDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}


	@Bean
	public DataSource routingDataSource(@Qualifier("writeDataSource") DataSource writeDataSource,
										@Qualifier("readDataSource") DataSource readDataSource) {
		ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();

		Map<Object, Object> dataSourceMap = new HashMap<>();
		dataSourceMap.put("write", writeDataSource);
		dataSourceMap.put("read", readDataSource);
		routingDataSource.setTargetDataSources(dataSourceMap);
		routingDataSource.setDefaultTargetDataSource(writeDataSource);

		return routingDataSource;
	}

	@Bean
	public DataSource routingLazyDataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
		return new LazyConnectionDataSourceProxy(routingDataSource);
	}

	@Bean
	public PlatformTransactionManager transactionManager(@Qualifier("routingLazyDataSource") DataSource dataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}
}
