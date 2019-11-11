package com.ilang.ad.gatewaytest.nacos;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.alibaba.nacos.NacosConfigProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@ConditionalOnProperty(prefix = "gateway.router.dynamic", name = "enabled", havingValue = "true")
public class DynamicRouteConfig {

	@Resource
	private ApplicationEventPublisher publisher;

	@Configuration
	@ConditionalOnProperty(prefix = "gateway.router.dynamic", name = "type", havingValue = "nacos", matchIfMissing = true)
	public class NacosDynRoute {

		@Resource
		private NacosConfigProperties nacosConfigProperties;

		@Bean
		public NacosRouteDefinitionRepository nacosRouteDefinitionRepository() {
			return new NacosRouteDefinitionRepository(publisher, nacosConfigProperties);
		}
	}
}
