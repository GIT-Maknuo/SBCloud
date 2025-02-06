package com.sbcloud.msvc.items.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.sbcloud.commons.entities.Product;
import com.sbcloud.msvc.items.models.ItemDto;

//@Primary //es una opcion para indicar que es la implementacion por defecto
@Service
public class ItemsServiceWebClient implements ItemService {

    private final WebClient webClient;
    
    public ItemsServiceWebClient(WebClient webClient) {
        this.webClient = webClient;
    }
    
	private ResponseSpec wclient(String method, String uri, Map<String, Object> variables, Object obj) {
    	HttpMethod httpMethod = null; 
    	try {
    		httpMethod = HttpMethod.valueOf(method.toUpperCase());
		} catch (Exception e) {			
			throw new IllegalArgumentException(e+"Protocolo indefinido");
		}
    	    	
    	if(httpMethod.matches("GET") || httpMethod.matches("DELETE")) {
        	return webClient
        			.method(httpMethod)    			
        			.uri(uri, variables)
        			.accept(MediaType.APPLICATION_JSON)
        			.retrieve(); 
    	}    	
    	if(httpMethod.matches("POST") || httpMethod.matches("PUT")) {
        	return webClient
        			.method(httpMethod)    			
        			.uri(uri, variables)
        			.accept(MediaType.APPLICATION_JSON)
        			.contentType(MediaType.APPLICATION_JSON)
        			.bodyValue((Product) obj)
        			.retrieve(); 
    	}

		return null;   			
    }

    @Override
    public List<ItemDto> findAll() {
        return wclient("GET", "/", new HashMap<>(), null)
            .bodyToFlux(Product.class)
            .map(p -> new ItemDto(p, new Random().nextInt(10)+1))
            .collectList().block();
    }

    @Override
    public Optional<ItemDto> findById(Long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
       // try { // se comenta para probar el circuit breaker
            return wclient("GET", "/{id}", map, null)
                .bodyToMono(Product.class)
                .map(p -> new ItemDto(p, new Random().nextInt(10)+1))
                .blockOptional();
       // } catch (Exception e) {
       //     return Optional.empty();
       // }
    }

	@Override
	public Product save(Product product) {
		return wclient("POST", "/", new HashMap<>(), product)
				.bodyToMono(Product.class)
				.block();
	}

	@Override
	public Product update(Product product, Long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
		return wclient("PUT", "/{id}", map, product)
				.bodyToMono(Product.class)
				.block();
	}

	@Override
	public void delete(Long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
		wclient("DELETE", "/{id}", map, null)
			.bodyToMono(Void.class)
			.block();
	}   
}