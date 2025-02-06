package com.sbcloud.oauth.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.sbcloud.oauth.models.User;

import io.micrometer.tracing.Tracer;

@Service
public class UsersService implements UserDetailsService{
	
	private final Logger log = LoggerFactory.getLogger(UsersService.class);
	
	private WebClient client;	
	
	//clase para manejar,editar, las trazas de micrometer
	private Tracer tracer;

	public UsersService(WebClient client,  Tracer tracer) {
		this.client = client;
		this.tracer = tracer;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	log.info("Metodo controller::loadUserByUsername(), user:{}", username);
		Map<String, String> param = new HashMap<>();
		param.put("username", username);
		try {
			User user = client
					.get()
					.uri("/username/{username}", param)
					.accept(MediaType.APPLICATION_JSON)
					.retrieve()
					.bodyToMono(User.class)
					.block();
			List<GrantedAuthority> roles = user.getRoles()
									.stream()
									.map(role -> new SimpleGrantedAuthority(role.getName()))
									.collect(Collectors.toList());				
			
	    	log.info("Esta haciendo loggin, user:{}", user);
	    	//se customiza el la traza
	    	tracer.currentSpan().tag("success.login message", "Esta haciendo loggin, user:{}" +" : "+ user);
			
			return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), 
					user.isEnabled(), true, true, true, roles);
		} catch (WebClientResponseException e) {
			String error = "No existe '"+ username+"' en sistema";
	    	log.error(error);
	    	//se customiza el la traza
	    	tracer.currentSpan().tag("error.login message", error +" : "+ e.getMessage());
			throw new UsernameNotFoundException(error);
		}
	}

}
