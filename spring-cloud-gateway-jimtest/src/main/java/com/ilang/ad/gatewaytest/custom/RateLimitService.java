package com.ilang.ad.gatewaytest.custom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Component
@ConditionalOnClass({MetricsEndpoint.class})
public class RateLimitService {

	@Resource
	private MetricsEndpoint metricsEndpoint;

	public boolean filter(ServerWebExchange exchange, String key, double expectedMax) {
		Double systemCpuUsage = metricsEndpoint.metric(key, null)
				.getMeasurements()
				.stream()
				.filter(Objects::nonNull)
				.findFirst()
				.map(MetricsEndpoint.Sample::getValue)
				.filter(Double::isFinite)
				.orElse(0.0D);

		boolean ok = systemCpuUsage < expectedMax;

		log.info("custom key: " + key + ", expected:" + expectedMax + ", actual:" + systemCpuUsage + " ok: " + ok);
		return ok;
	}

}