package com.ilang.ad.gatewaytest.bodymodify;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class BodyModifyService {

	@Resource
	private LogStandardGatewayService logStandardGatewayService;

	public Mono<JSONObject> modifyResBody(ServerWebExchange e, Object s) {
		JSONObject json = null;
		if (s instanceof JSONObject) {
			json = (JSONObject) s;
		} else if (s instanceof Map) {
			json = new JSONObject((Map) s);
		} else {
			json = new JSONObject();
			json.put("body", s);
		}
		json.put("author", "jimjimjimjimjimjimjimjimjimjimjim");

		this.logStandardGatewayService.outLog(e, json);

		return Mono.just(json);
	}

}
