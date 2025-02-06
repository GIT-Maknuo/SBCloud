package com.sbcloud.msvc.items;

import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

//esta es otra forma de consumir servicios y comunicar microservicios "Webclient"
//hay que crear un bean para que se pueda inyectar en los servicios
@Configuration
public class WebClientConfig {
    
    
    @Bean
    WebClient webCBuilder(WebClient.Builder webClient,
    		@Value("${msvc-products.baseurl}") String BASE_URL,
    		ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        return webClient.baseUrl(BASE_URL).filter(lbFunction).build();
    }
    
//    @Bean
//    @LoadBalanced
//    WebClient.Builder webClientBuilder() {
//        return WebClient.builder().baseUrl(BASE_URL);
//    }
}
