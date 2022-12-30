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

import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
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
		Class<? extends TestDatabaseManager> clazz = withDatabase.databaseManager();
		TestDatabaseManager dbManager = BeanUtils.instantiateClass(clazz);
		return new TestDatabaseContextCustomizer(testClass, dbManager);
	}

	static class TestDatabaseContextCustomizer implements ContextCustomizer {
		private final Class<?> testClass;

		private final TestDatabaseManager databaseManager;

		public TestDatabaseContextCustomizer(Class<?> testClass, TestDatabaseManager databaseManager) {
			this.testClass = testClass;
			this.databaseManager = databaseManager;
		}

		@Override
		public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
			Map<String, Object> properties = this.databaseManager.process(this.testClass);
			MutablePropertySources sources = context.getEnvironment().getPropertySources();
			MapPropertySource propertySource = new MapPropertySource("DB property source", properties);
			sources.addFirst(propertySource);

			((BeanDefinitionRegistry) context.getBeanFactory()).registerBeanDefinition("testDatabaseManagerShutdown",
					BeanDefinitionBuilder.genericBeanDefinition(DisposableBean.class, () -> this.databaseManager::shutdown).getBeanDefinition()
			);
		}
	}
}
