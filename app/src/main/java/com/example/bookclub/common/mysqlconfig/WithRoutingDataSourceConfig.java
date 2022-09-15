package com.example.bookclub.common.mysqlconfig;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

//@Configuration
public class WithRoutingDataSourceConfig {
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari.write")
	public DataSource writeDataSource() {
		return DataSourceBuilder
				.create()
				.type(HikariDataSource.class)
				.build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari.read")
	public DataSource readDataSource() {
		return DataSourceBuilder
				.create()
				.type(HikariDataSource.class)
				.build();
	}

	/**
	 * {@link org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource}는
	 * {@link org.springframework.beans.factory.InitializingBean}을 구현하므로,
	 * 명시적으로 afterPropertiesSet()메소드를 호출하거나
	 * 별도 @Bean으로 만들어 Spring Life Cycle을 타도록 해야 한다.
	 */
	@Bean
	public DataSource routingDataSource(@Qualifier("writeDataSource") DataSource writeDataSource,
										@Qualifier("readDataSource") DataSource readDataSource) {
		ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();

		Map<Object, Object> dataSourceMap = new HashMap<Object, Object>();
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
