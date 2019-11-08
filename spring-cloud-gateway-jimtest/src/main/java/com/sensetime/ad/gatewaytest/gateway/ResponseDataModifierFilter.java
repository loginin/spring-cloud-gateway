package com.sensetime.ad.gatewaytest.gateway;

import com.alibaba.fastjson.JSONObject;
import com.sun.tools.doclets.standard.Standard;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 修改后端server返回的数据. 当前实例将读取原始数据并组装成json 其他场景: 编解码  * @author duanzongliang@sensetime.com
 * <p>
 * TODO: WARNING: 这里差不多每5个请求会有一次请求的数据分两次发送的问题; 官方自带功能没问题
 */
//@Component
public class ResponseDataModifierFilter implements GlobalFilter, Ordered {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpResponse originalResponse = exchange.getResponse();
		DataBufferFactory bufferFactory = originalResponse.bufferFactory();
		ServerHttpResponse response = new ServerHttpResponseDecorator(originalResponse) {
			@Override
			public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
				if (!(body instanceof Flux)) {
					return super.writeWith(body);
				}
				Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
				return super.writeWith(fluxBody.map(dataBuffer -> {
					byte[] content = new byte[dataBuffer.readableByteCount()];
					dataBuffer.read(content);
					// 释放掉内存
					DataBufferUtils.release(dataBuffer);
					String resp = new String(content, StandardCharsets.UTF_8);
					JSONObject json = null;
					try {
						json = JSONObject.parseObject(resp);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					if (json == null) {
						json = new JSONObject();
						json.put("modified-body", resp);
					}
					json.put("modified-code", 200);
					byte[] newRs = json.toJSONString().getBytes(Charset.forName("UTF-8"));
					// 如果不重新设置长度则收不到消息

					try {
						originalResponse.getHeaders().setContentLength(newRs.length);
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
					return bufferFactory.wrap(newRs);
				}));
			}
		};
		return chain.filter(exchange.mutate().response(response).build());
	}

	@Override
	public int getOrder() {
		// -1是返回数据, -2先执行
		return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 20;
	}

}
