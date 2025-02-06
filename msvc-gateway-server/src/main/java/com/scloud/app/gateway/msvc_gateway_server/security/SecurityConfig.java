package com.scloud.app.gateway.msvc_gateway_server.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;;

@Configuration
public class SecurityConfig {
	
	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception{
		
		return http.authorizeExchange(authz->{
			authz.pathMatchers("/authorized", "/logout").permitAll()
			.pathMatchers(HttpMethod.GET, "/api/items", "/api/products", "/api/users").permitAll()
			.pathMatchers(HttpMethod.GET, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}")
			.hasAnyRole("ADMIN", "USER")
			.pathMatchers("/api/products/**", "/api/items/**", "/api/users/**").hasRole("ADMIN")//esta equivale a las tres siguientes
//			.pathMatchers(HttpMethod.POST,"/api/products", "/api/items", "/api/users").hasRole("ADMIN")
//			.pathMatchers(HttpMethod.PUT,"/api/products/{id}", "/api/items/{id}", "/api/users/{id}").hasRole("ADMIN")
//			.pathMatchers(HttpMethod.DELETE,"/api/products/{id}", "/api/items/{id}", "/api/users/{id}").hasRole("ADMIN")
			.anyExchange().authenticated();
		}).cors(csrf->csrf.disable())
				.oauth2Login(withDefaults())
				.oauth2Client(withDefaults())
				.oauth2ResourceServer(
//						oauth2->oauth2.jwt(withDefaults()))
				oauth2->oauth2.jwt(
						jwt -> jwt
								.jwtAuthenticationConverter(new Converter<Jwt, Mono<AbstractAuthenticationToken>>() {

							@Override
							public Mono<AbstractAuthenticationToken> convert(Jwt source) {
								Collection<String> roles = source.getClaimAsStringList("roles");
								Collection<GrantedAuthority> authorities = roles.stream()
										.map(role -> new SimpleGrantedAuthority(role))
										.collect(Collectors.toList());
								return Mono.just(new JwtAuthenticationToken(source,authorities));
							}
							
						}) ))
				.build();
	}

}
