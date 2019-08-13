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

package com.alibaba.cloud.stream.binder.rocketmq;

import static org.apache.rocketmq.spring.support.RocketMQHeaders.PREFIX;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 * @author <a href="mailto:jiashuai.xie01@gmail.com">Xiejiashuai</a>
 */
public interface RocketMQBinderConstants {

	/**
	 * Header key
	 */
	String ROCKET_TRANSACTIONAL_ARG = "TRANSACTIONAL_ARG";

	/**
	 * Default value
	 */
	String DEFAULT_NAME_SERVER = "127.0.0.1:9876";

	String DEFAULT_GROUP = PREFIX + "binder_default_group_name";

	/**
	 * RocketMQ re-consume times
	 */
	String ROCKETMQ_RECONSUME_TIMES = PREFIX + "RECONSUME_TIMES";

}
