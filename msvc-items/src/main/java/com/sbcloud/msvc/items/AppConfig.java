package com.sbcloud.msvc.items;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@Configuration
public class AppConfig {
	
	@Bean
	Customizer<Resilience4JCircuitBreakerFactory> customizerCB(){			
		return (factory)-> factory.configureDefault(id->{
			return new Resilience4JConfigBuilder(id).circuitBreakerConfig(CircuitBreakerConfig
					.custom()
					.slidingWindowSize(10)//maneja el numero de peticiones a considerar para tomar abierto el CB
					.failureRateThreshold(50)
					.waitDurationInOpenState(Duration.ofSeconds(10L))//tiempo que dura abierto CB 
					.permittedNumberOfCallsInHalfOpenState(5)//numero de llamadas permitidas en estado semiabierto
					.slowCallDurationThreshold(Duration.ofSeconds(2l))//maneja tiempo al que se considerara como lento la respuesta de un servicio
					.slowCallRateThreshold(50)//numero de llamadas lentas en las cuales se cosiderara CB sea abierto
					.build())
					.timeLimiterConfig(TimeLimiterConfig
					.custom()
					.timeoutDuration(Duration.ofSeconds(3l))//maneja el tiempo limite de respuesta de service
					.build())
			.build();							
		});
	}

}
