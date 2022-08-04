/*
 * Copyright 2013-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.tests.nacos.config;

import com.alibaba.cloud.testsupport.*;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.io.*;
import java.util.*;

import static com.alibaba.cloud.testsupport.Constant.REFRESH_CONFIG;
import static com.alibaba.cloud.testsupport.Constant.TIME_OUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 *
 * Test function: Nacos config refresh.
 *
 * @author freeman
 */
//@HasDockerAndItEnabled
@SpringCloudAlibaba(composeFiles = "docker/nacos-compose-test.yml", serviceName = "nacos-standalone")
@TestExtend(time = 3* TIME_OUT)
public class NacosConfigRefreshTest {
	
	@Mock
	protected ConfigService service1;
	
	
	@BeforeAll
	public static void setUp(){
	
	}
	
	@BeforeEach
	public void prepare()  throws NacosException {
		Properties nacosSettings = new Properties();
		String serverIp8 = "127.0.0.1:8848";
		nacosSettings.put(PropertyKeyConst.SERVER_ADDR, serverIp8);
		nacosSettings.put(PropertyKeyConst.USERNAME, "nacos");
		nacosSettings.put(PropertyKeyConst.PASSWORD, "nacos");
		
		service1 = ConfigFactory.createConfigService(nacosSettings);
		
	}

	@Test
	public void testRefreshConfig() throws InterruptedException {
		// make sure everything is ready !
		Thread.sleep(2000L);

		Tester.testFunction("Dynamic refresh config", () -> {
			// update config
			updateConfig();
			
			// wait config refresh
			Thread.sleep(2000L);
			String content = service1.getConfig("nacos-config-refresh.yml", "DEFAULT_GROUP", TIME_OUT);
			
			ClassPathResource classPathResource = new ClassPathResource(REFRESH_CONFIG);
			File file = classPathResource.getFile();
			
			final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			sb.deleteCharAt(sb.length()-1);
			assertThat(content).isEqualTo(sb.toString());
		});
	}

	private void updateConfig() throws NacosException {
		service1.publishConfig("nacos-config-refresh.yml", "DEFAULT_GROUP",
				"configdata:\n" +
					"  user:\n" +
					"    age: 22\n" +
					"    name: freeman1123\n" +
					"    map:\n" +
					"      hobbies:\n" +
					"        - art\n" +
					"        - programming\n" +
					"        - movie\n" +
					"      intro: Hello, I'm freeman\n" +
					"      extra: yo~\n" +
					"    users:\n" +
					"      - name: dad\n" +
					"        age: 20\n" +
					"      - name: mom\n" +
					"        age: 18",
				"yaml");
	}
}
