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

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.TestContextAnnotationUtils;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestDatabaseContextCustomizerFactory implements ContextCustomizerFactory {

	@Override
	public ContextCustomizer createContextCustomizer(Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
		WithDatabase withDatabase = TestContextAnnotationUtils.findMergedAnnotation(testClass, WithDatabase.class);
		if (withDatabase == null) {
			return null;
		}
		// TODO: add a lifecycle callback to stop the container
		Class<? extends TestDatabaseManager> clazz = withDatabase.databaseManager();
		TestDatabaseManager dbManager = BeanUtils.instantiateClass(clazz);
		Map<String, Object> properties = dbManager.process(testClass);
		return new TestDatabaseContextCustomizer(properties);
	}

	static class TestDatabaseContextCustomizer implements ContextCustomizer {

		private final Map<String, Object> properties;

		public TestDatabaseContextCustomizer(Map<String, Object> properties) {
			this.properties = properties;
		}

		@Override
		public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
			MutablePropertySources sources = context.getEnvironment().getPropertySources();
			MapPropertySource propertySource = new MapPropertySource("DB property source", this.properties);
			sources.addFirst(propertySource);
		}
	}
}
