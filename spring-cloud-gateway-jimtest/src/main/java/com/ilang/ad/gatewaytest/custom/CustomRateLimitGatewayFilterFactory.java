package com.ilang.ad.gatewaytest.custom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Slf4j
public class CustomRateLimitGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {

	@Resource
	RateLimitService rateLimitService;

	@Override
	public GatewayFilter apply(NameValueConfig config) {
		return new GatewayFilter() {
			@Override
			public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
				double doubleVal = -1d;
				try {
					if (config.getValue() != null) {
						doubleVal = Double.parseDouble(config.getValue());
					}
				} catch (Exception e) {
					log.error("value is not integer:{}", config.getValue());
				}
				if (rateLimitService.filter(exchange, config.getName(), doubleVal)) {
					return chain.filter(exchange);
				} else {
					exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
					exchange.getResponse().getHeaders().add("filtered-reason",
							"jim-custom-rate-limit-201911===hoho==reason:" + config.getName() + ", value:" + doubleVal);
					return exchange.getResponse().setComplete();
				}
			}
		};
	}
}
