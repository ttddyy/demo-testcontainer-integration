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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Tadaya Tsuyukubo
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WithDatabase(databaseManager = MySqlManager.class)
public @interface WithMySql {

	String databaseName() default "";

	String dockerImageName() default "mysql:8";

	String username() default "";

	String password() default "";

	int port() default 0;

	/**
	 * Used to check whether local DB is running.
	 * If this url doesn't work, start a test container.
	 *
	 * @return local JDBC url
	 */
	// TODO: dynamically compose
	String localJdbcUrl() default "";

}
