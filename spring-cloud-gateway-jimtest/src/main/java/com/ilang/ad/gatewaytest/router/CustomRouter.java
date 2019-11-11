package com.ilang.ad.gatewaytest.router;

import com.alibaba.fastjson.JSONObject;
import com.ilang.ad.gatewaytest.bodymodify.BodyModifyService;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Configuration
public class CustomRouter {

	@Resource
	private BodyModifyService bodyModifyService;

	String REMOTE_URI = "http://127.0.0.1:39999";

	/**
	 * request和response的执行顺序和定义顺序无关; request的filter或predicate按照定义的顺序来
	 */
	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		RouteLocatorBuilder.Builder builder1 = builder.routes();
		this.routeBodyModify(builder1);
		this.routeNacos(builder1);
		this.routeCommonConf(builder1);
		return builder1.build();
	}

	private void routeBodyModify(RouteLocatorBuilder.Builder builder1) {
		builder1.route("reqrespmodify", r -> {
			return r.host("testabc.anotherhost.org")
					.and().path("/ifun/**")
					.filters(f -> {
						return f
//								.prefixPath("/ifun/ssp/**")
								.setPath("/api/ifun/ssp")
//						.changeRequestUri(e -> Optional.of(URI.create(REMOTE_URI + "/api/ifun/changeUrl")))
//						.setStatus(404)
								.modifyResponseBody(Object.class, JSONObject.class, this.bodyModifyService::modifyResBody)
								.modifyRequestBody(String.class, String.class, ((exchange, s) -> Mono.just(s.toUpperCase())))
								.hystrix((e) -> e.setName("fallbackcmd").setFallbackUri("forward:/hystrix/fallback"))
								;
					}).uri(REMOTE_URI);
		});
	}

	private void routeNacos(RouteLocatorBuilder.Builder builder1) {
		builder1.route("nacos", r -> {
			return r.path("/nacos/**")
					.filters(f -> {
						return f
								.addResponseHeader("nacos", "yes")
								.addRequestHeader("nacos-2-backend", "yes")
								.setStatus(200)
								;
					})
					.uri(REMOTE_URI);
		});
	}

	private void routeCommonConf(RouteLocatorBuilder.Builder builder1) {
		builder1.route("common-go", r -> {
			return r.host("ribbon.org").filters(f->{
				return f.setStatus(200);
			}).uri(REMOTE_URI);
		});
	}

}
