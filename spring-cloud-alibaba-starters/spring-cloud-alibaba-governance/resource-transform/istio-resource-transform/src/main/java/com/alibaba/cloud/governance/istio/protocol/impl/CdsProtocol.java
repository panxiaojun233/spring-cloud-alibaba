/*
 * Copyright 2013-2018 the original author or authors.
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

package com.alibaba.cloud.governance.istio.protocol.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.cloud.governance.istio.XdsChannel;
import com.alibaba.cloud.governance.istio.XdsScheduledThreadPool;
import com.alibaba.cloud.governance.istio.constant.IstioConstants;
import com.alibaba.cloud.governance.istio.protocol.AbstractXdsProtocol;
import io.envoyproxy.envoy.config.cluster.v3.Cluster;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;

/**
 * @author musi
 * @author <a href="liuziming@buaa.edu.cn"></a>
 */
public class CdsProtocol extends AbstractXdsProtocol<Cluster> {

	public CdsProtocol(XdsChannel xdsChannel,
			XdsScheduledThreadPool xdsScheduledThreadPool, int pollingTime) {
		super(xdsChannel, xdsScheduledThreadPool, pollingTime);
	}

	@Override
	protected List<Cluster> decodeXdsResponse(DiscoveryResponse response) {
		List<Cluster> clusters = new ArrayList<>();
		for (com.google.protobuf.Any res : response.getResourcesList()) {
			try {
				Cluster cluster = res.unpack(Cluster.class);
				clusters.add(cluster);
			}
			catch (Exception e) {
				log.error("unpack cluster failed", e);
			}
		}
		return clusters;
	}

	public Set<String> getEndPointNames(List<Cluster> clusters) {
		Set<String> endpoints = new HashSet<>();
		if (clusters == null) {
			return endpoints;
		}
		for (Cluster cluster : clusters) {
			cluster.getEdsClusterConfig().getServiceName();
			endpoints.add(cluster.getEdsClusterConfig().getServiceName());
		}
		return endpoints;
	}

	@Override
	public String getTypeUrl() {
		return IstioConstants.CDS_URL;
	}

}