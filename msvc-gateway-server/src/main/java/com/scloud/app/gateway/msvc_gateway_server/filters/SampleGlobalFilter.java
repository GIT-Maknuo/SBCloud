package com.scloud.app.gateway.msvc_gateway_server.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

//FILTRO GLOBALES QUE ABARCAN TODOS LOS MICROSERVICIOS ENRUTADOS 
@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered {
	
	private final Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		logger.info("Llmando filtro SampleGlobalFilter::filter");
		exchange.getRequest().mutate().headers(h -> h.add("token", "123456789"));
		
		return chain.filter(exchange).then(Mono.fromRunnable(() ->{
			logger.info("EJECUTANDO EL FILTRO POST RESPONSE");
			
			Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token")).ifPresent(value ->{
				logger.info("token: " + value);
				exchange.getResponse().getHeaders().add("token", value);
			});			
			
			exchange.getResponse().getCookies()
				.add("color", ResponseCookie.from("color", "red").build());
//			exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
		}));
	}

	@Override
	public int getOrder() {
		return 100;
	}

}
