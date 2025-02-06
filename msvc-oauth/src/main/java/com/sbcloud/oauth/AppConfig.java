package com.sbcloud.oauth;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@LoadBalanced
	WebClient webClient(WebClient.Builder wc,
			ReactorLoadBalancerExchangeFilterFunction lbFilterFunction){
		return wc.baseUrl("http://msvc-users").filter(lbFilterFunction).build();
	}
	
//	@Bean
//	@LoadBalanced
//	WebClient.Builder webClient(){
//		return WebClient.builder().baseUrl("http://msvc-users");
//	}

}
