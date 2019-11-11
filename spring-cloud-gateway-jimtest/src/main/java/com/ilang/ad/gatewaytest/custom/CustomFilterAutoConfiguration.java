package com.ilang.ad.gatewaytest.custom;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 自定义filter
 */
@Component
public class CustomFilterAutoConfiguration {

	@Bean
	CustomRateLimitGatewayFilterFactory customRateLimitGatewayFilterFactory() {
		return new CustomRateLimitGatewayFilterFactory();
	}

}
