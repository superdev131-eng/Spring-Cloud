/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.alibaba.dubbo.metadata;

import org.springframework.cloud.alibaba.dubbo.annotation.DubboTransported;

import java.util.Objects;

/**
 * {@link DubboTransported @DubboTransported} Metadata
 */
public class DubboTransportedMetadata {

    private String protocol;

    private String cluster;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DubboTransportedMetadata)) return false;
        DubboTransportedMetadata that = (DubboTransportedMetadata) o;
        return Objects.equals(protocol, that.protocol) &&
                Objects.equals(cluster, that.cluster);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol, cluster);
    }
}
