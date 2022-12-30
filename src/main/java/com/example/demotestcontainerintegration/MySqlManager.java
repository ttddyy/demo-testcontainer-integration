/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demotestcontainerintegration;

import java.sql.DriverManager;
import java.util.Map;

import org.testcontainers.containers.MySQLContainer;

import org.springframework.test.context.TestContextAnnotationUtils;

/**
 * @author Tadaya Tsuyukubo
 */
public class MySqlManager implements TestDatabaseManager {

	private static final String SPRING_DB_PROPERTY = "spring.datasource.url";

	private MySQLContainer container;

	@Override
	public Map<String, Object> start(Class<?> testClass) {
		WithMySql withMySql = TestContextAnnotationUtils.findMergedAnnotation(testClass, WithMySql.class);
		if (withMySql == null) {
			return null;
		}

		String localJdbcUrl = withMySql.localJdbcUrl();
		if (isLocalDbRunning(localJdbcUrl)) {
			return Map.of(SPRING_DB_PROPERTY, localJdbcUrl);
		}
		startContainer(withMySql);
		return Map.of(SPRING_DB_PROPERTY, this.container.getJdbcUrl());
	}

	private boolean isLocalDbRunning(String localJdbcUrl) {
		try {
			// check local mysql is running or not
			DriverManager.getConnection(localJdbcUrl);
			return true;
		}
		catch (Exception ex) {
			// TODO: log
		}
		return false;
	}

	private void startContainer(WithMySql withMySql) {
		if (this.container == null) {
			this.container = new MySQLContainer<>(withMySql.dockerImageName())
					.withDatabaseName(withMySql.databaseName())
					.withUsername(withMySql.username())
					.withPassword(withMySql.password())
					.withExposedPorts(withMySql.port())
					.withReuse(true);
		}
		if (!this.container.isRunning()) {
			this.container.start();
		}
	}

	@Override
	public void shutdown() {
		if (this.container != null && this.container.isRunning()) {
			this.container.stop();
		}
	}
}
