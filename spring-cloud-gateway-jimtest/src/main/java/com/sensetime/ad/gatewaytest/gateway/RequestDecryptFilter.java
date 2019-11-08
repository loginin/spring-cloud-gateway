package com.sensetime.ad.gatewaytest.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.ByteBufAllocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 *  * @author duanzongliang@sensetime.com  
 */
// @Component
public class RequestDecryptFilter implements GlobalFilter {

	@Autowired
	private ObjectMapper objectMapper;

	private final DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(
			ByteBufAllocator.DEFAULT);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest originalRequest = exchange.getRequest();
		ServerHttpRequest request = originalRequest;
		if (originalRequest.getMethod() == HttpMethod.GET
				|| originalRequest.getMethod() == HttpMethod.POST) {
			URI uri = exchange.getRequest().getURI();
			StringBuilder query = new StringBuilder();
			String originalQuery = uri.getRawQuery();

			if (StringUtils.hasText(originalQuery)) {
				query.append(originalQuery);
			}
			String added = "&dddsdsdw=queryjimgateway";
			query.append(added);
			int length = query.length();
			length = (int) (request.getHeaders().getContentLength() + added.length());
			try {
				URI newUri = UriComponentsBuilder.fromUri(uri)
						.replaceQuery(query.toString()).build(true).toUri();

				ServerHttpRequest newRequest = exchange.getRequest().mutate().uri(newUri)
						.build();
				newRequest.getHeaders().setContentLength(length);

				return chain.filter(exchange.mutate().request(newRequest).build());
			}
			catch (RuntimeException ex) {
				throw new IllegalStateException(
						"Invalid URI query: \"" + query.toString() + "\"");
			}
		}
		else if (originalRequest.getMethod() == HttpMethod.POST) {
			// 在body里增加来源参数
			// request = new ServerHttpRequestDecorator(originalRequest) {
			// @Override
			// public Flux<DataBuffer> getBody() {
			// Flux<DataBuffer> body = super.getBody();
			// StringHolder holder = new StringHolder();
			// body.subscribe(dataBuffer -> {
			// byte[] content = new byte[dataBuffer.readableByteCount()];
			// dataBuffer.read(content);
			// holder.content = new String(content, Charset.forName("UTF-8"));
			// });
			//
			// DataBuffer dataBuffer = dataBufferFactory.allocateBuffer();
			// String vv = holder.content + "&post-jim-gateway";
			// System.out.println("hc:" + holder.content);
			// System.out.println(vv);
			// dataBuffer.write(vv.getBytes());
			// return Flux.just(dataBuffer);
			// }
			// };
			request = new ServerHttpRequestDecorator(originalRequest) {

				@Override
				public Flux<DataBuffer> getBody() {
					Flux<DataBuffer> body = super.getBody();
					InputStreamHolder holder = new InputStreamHolder();
					body.subscribe(buffer -> holder.inputStream = buffer.asInputStream());
					if (null != holder.inputStream) {
						try {
							// 解析JSON的节点
							JsonNode jsonNode = objectMapper.readTree(holder.inputStream);
							ObjectNode objectNode = (ObjectNode) jsonNode;
							// JSON节点最外层写入新的属性
							objectNode.put("userId", "dddd");
							DataBuffer dataBuffer = dataBufferFactory.allocateBuffer();
							String json = objectNode.toString();
							dataBuffer.write(json.getBytes(StandardCharsets.UTF_8));
							return Flux.just(dataBuffer);
						}
						catch (Exception e) {
							e.printStackTrace();
							throw new IllegalStateException(e);
						}
					}
					else {
						return super.getBody();
					}
				}
			};
		}
		return chain.filter(exchange.mutate().request(request).build());
	}

	private class StringHolder {

		String content;

	}

	private class InputStreamHolder {

		InputStream inputStream;

	}

}
