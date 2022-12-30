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

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */

@SpringJUnitConfig
@WithMySql(username = "root", password = "password", port = 3306, databaseName = "my_db", localJdbcUrl = "jdbc:mysql://localhost:3306/my_db?user=root&password=password")
class MyTests {

	@Value("${spring.datasource.url}")
	String jdbcUrl;

	@Test
	void check() {
		assertThat(this.jdbcUrl).isNotEmpty();
		System.out.println("JDBC URL=" + this.jdbcUrl);
	}
}
