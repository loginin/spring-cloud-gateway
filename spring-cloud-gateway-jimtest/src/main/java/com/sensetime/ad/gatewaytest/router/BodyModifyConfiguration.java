package com.sensetime.ad.gatewaytest.router;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 *  * @author duanzongliang@sensetime.com
 *  
 */
@Configuration
public class BodyModifyConfiguration {

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes().route("bsdsd,sndkjsd", r -> {
			return r.host("testabc.anotherhost.org").filters(f -> {
				return f.prefixPath("/sensefocus/ssp/")
						.modifyRequestBody(String.class, String.class, ((exchange, s) -> {
							return Mono.just(s.toUpperCase());
						}));
			}).uri("http://127.0.0.1:9999");
		}).build();
	}

}
