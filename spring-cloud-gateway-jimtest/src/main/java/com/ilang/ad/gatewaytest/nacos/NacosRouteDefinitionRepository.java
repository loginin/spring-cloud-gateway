package com.ilang.ad.gatewaytest.nacos;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.alibaba.nacos.NacosConfigProperties;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class NacosRouteDefinitionRepository implements RouteDefinitionRepository {

	private static final String GATEWAY_DATA_ID = "gateway_routes";

	private static final String GATEWAY_GROUP_ID = "GATEWAY_GROUP";

	private ApplicationEventPublisher publisher;

	private NacosConfigProperties nacosConfigProperties;

	public NacosRouteDefinitionRepository(ApplicationEventPublisher publisher, NacosConfigProperties nacosConfigProperties) {
		this.publisher = publisher;
		this.nacosConfigProperties = nacosConfigProperties;
		this.addNacosListener();
	}

	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {
		try {
			String content = this.nacosConfigProperties.configServiceInstance().getConfig(GATEWAY_DATA_ID, GATEWAY_GROUP_ID, 2000);
			System.out.println("nacos discovery content:" + content);
			List<RouteDefinition> routeDefinitions = getListByStr(content);
			return Flux.fromIterable(routeDefinitions);
		} catch (NacosException e) {
			e.printStackTrace();
		}
		return Flux.fromIterable(new ArrayList<>());
	}

	@Override
	public Mono<Void> save(Mono<RouteDefinition> route) {
		return null;
	}

	@Override
	public Mono<Void> delete(Mono<String> routeId) {
		return null;
	}

	private void addNacosListener() {
		try {
			this.nacosConfigProperties.configServiceInstance().addListener(GATEWAY_DATA_ID, GATEWAY_GROUP_ID, new Listener() {
				@Override
				public Executor getExecutor() {
					return null;
				}

				@Override
				public void receiveConfigInfo(String configInfo) {
					//当nacos有数据变化时
					publisher.publishEvent(new RefreshRoutesEvent(this));
				}
			});
		} catch (NacosException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private List<RouteDefinition> getListByStr(String content) {
		if (StringUtils.isNotEmpty(content)) {
			return JSONObject.parseArray(content, RouteDefinition.class);
		}
		return new ArrayList<>(0);
	}
}
