package com.scloud.app.gateway.msvc_gateway_server.filters;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;


@Component
public class SampleCookieGatewayFilterFactory extends AbstractGatewayFilterFactory<SampleCookieGatewayFilterFactory.Cookie>{

	private final Logger logger = LoggerFactory.getLogger(SampleCookieGatewayFilterFactory.class);
	
	public SampleCookieGatewayFilterFactory() {
		super(Cookie.class);
	}
	
	@Override
	public GatewayFilter apply(Cookie config) {
		return new OrderedGatewayFilter(
			(exchange, chain) -> {
				logger.info("EJECUTANDO PRE filter factory: " + config.getMessage());

				return chain.filter(exchange).then(Mono.fromRunnable(() -> {
					Optional.of(config.value).ifPresent(value -> {
						exchange.getResponse().addCookie(ResponseCookie.from(config.name, value).build());
					});
					logger.info("EJECUTANDO POST filter factory: " + config.getMessage());
				}));
			}, 100);
	}	
	
	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList("name", "value", "message");
	}

	public static class Cookie {
		private String name;
		private String value;
		private String message;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}

	}



}
