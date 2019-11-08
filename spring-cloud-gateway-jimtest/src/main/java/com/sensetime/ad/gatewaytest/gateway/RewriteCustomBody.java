package com.sensetime.ad.gatewaytest.gateway;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 *  * @author duanzongliang@sensetime.com  * @date 2019/10/25 18:32  
 */
@Component
public class RewriteCustomBody implements RewriteFunction<String, String> {

	@Override
	public Publisher<String> apply(ServerWebExchange exchange, String requestBody) {
		return Mono.just("abc----" + requestBody);
	}

}
