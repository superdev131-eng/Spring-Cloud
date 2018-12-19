/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.alibaba.nacos;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.cloud.alibaba.nacos.refresh.NacosContextRefresher;
import org.springframework.cloud.alibaba.nacos.refresh.NacosRefreshHistory;
import org.springframework.cloud.alibaba.nacos.refresh.NacosRefreshProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author juven.xuxb
 */
@Configuration
public class NacosConfigAutoConfiguration {

	@Bean
	public NacosConfigProperties nacosConfigProperties(ApplicationContext context) {
		if (context.getParent() != null
				&& BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
						context.getParent(), NacosConfigProperties.class).length > 0) {
			return BeanFactoryUtils.beanOfTypeIncludingAncestors(context.getParent(),
					NacosConfigProperties.class);
		}
		NacosConfigProperties nacosConfigProperties = new NacosConfigProperties();
		return nacosConfigProperties;
	}

	@Bean
	public NacosRefreshProperties nacosRefreshProperties() {
		return new NacosRefreshProperties();
	}

	@Bean
	public NacosRefreshHistory nacosRefreshHistory() {
		return new NacosRefreshHistory();
	}

	@Bean
	public NacosContextRefresher nacosContextRefresher(
			NacosConfigProperties nacosConfigProperties,
			NacosRefreshProperties nacosRefreshProperties,
			NacosRefreshHistory refreshHistory,
			NacosPropertySourceRepository propertySourceRepository) {
		return new NacosContextRefresher(nacosRefreshProperties, refreshHistory,
				propertySourceRepository, nacosConfigProperties.configServiceInstance());
	}
}
