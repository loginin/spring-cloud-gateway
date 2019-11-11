package com.ilang.ad.gatewaytest.bodymodify;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class LogStandardGatewayService {

	void outLog(ServerWebExchange exchange, JSONObject body) {
		JSONObject json = new JSONObject();
		this.fillRequest(exchange.getRequest(), json);
		this.fillResponse(exchange.getResponse(), json, body);
		this.fillOther(exchange.getResponse(), json);
		System.out.println("打印标准日志:" + json.toJSONString());
	}

	private void fillRequest(ServerHttpRequest request, JSONObject json) {
		json.put("method", request.getMethodValue());
		json.putAll(request.getHeaders().toSingleValueMap());
		json.put("path", request.getPath().value());
	}

	private void fillResponse(ServerHttpResponse response, JSONObject json, JSONObject body) {
		if (response != null) {
			json.put("resp_status", response.getStatusCode() == null ? "-1" : response.getStatusCode());
			json.put("resp_data", body);
		}
	}

	private void fillOther(ServerHttpResponse response, JSONObject json) {
		String other = response.getHeaders().getFirst("other");
		if (other != null) {
			json.put("other", other);
		}
	}

}
