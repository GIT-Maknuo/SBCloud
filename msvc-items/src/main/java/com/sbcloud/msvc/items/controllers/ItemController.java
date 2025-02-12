package com.sbcloud.msvc.items.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbcloud.commons.entities.Product;
import com.sbcloud.msvc.items.models.ItemDto;
import com.sbcloud.msvc.items.services.ItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@SuppressWarnings("rawtypes")
@RefreshScope
@RestController
public class ItemController {
	
	private final Logger log = LoggerFactory.getLogger(ItemController.class);
	
    private final ItemService service;

	private final CircuitBreakerFactory cbFactory;
	
	private final Product GlobalProd = new Product(null, "CIRCUIT-BREAKER", 10.0, LocalDate.now(), null);
	
	//trae configuraciones del servidor de configuraciones
	//@Value("${configuracion.texto}")
	private String text;	

	private final Environment env;


    //seria con Qualifier decir que implementacion de ItemService se va a inyectar
    public ItemController(@Qualifier("itemsServiceWebClient")ItemService service, 
    						CircuitBreakerFactory cbFactory,
    						Environment env) {
        this.cbFactory = cbFactory;
    	this.service = service;
    	this.env = env;
    }
    
	@GetMapping("/fetch-configs")
	public ResponseEntity<?>fectchConfigs(@Value("${server.port}") String port){
		Map<String, String> json = new HashMap<>();
		json.put("text", text);
		json.put("port", port);

		log.info(port);
		log.info(text);
		if(env.getActiveProfiles().length>0 && env.getActiveProfiles()[0].equals("dev")) {
			json.put("autor", env.getProperty("configuracion.autor.nombre"));
			json.put("email", env.getProperty("configuracion.autor.email"));
		}
		
		return ResponseEntity.ok(json);
	} 

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(name = "name", required = false) String name,
                                  @RequestHeader(name = "token-request", required = false) String token) {
    	//los parametros name y token vienen de configuracion de gateway 
    	//de los filtros en el archivo application.yml
    	System.out.println("name: " + name);
    	System.out.println("token: " + token);
		//micrometer
    	log.info("Llamada a metodo ItemController::list()");
    	log.info("Request parameter:{}",name);
    	log.info("Token:{}",token);
        List<?> items = service.findAll();
        return items.isEmpty() ? 
            ResponseEntity.noContent().build() : 
            ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable long id) {
        Optional<ItemDto> item = cbFactory.create("items")
        		.run(()-> service.findById(id), e -> {
        			log.error("Error al consultar el item", e.getMessage());
        			Product product = GlobalProd;
        			product.setId(1l);
        			return Optional.ofNullable(new ItemDto(product, 5));
        		});
        return item.isPresent() ? 
            ResponseEntity.ok(item.get()) : 
            ResponseEntity.notFound().build();
    }
    
    //con esta anotacion le indicamos que la configuracion de CB 
    //a tomar es la hecha en application.properties o application.yml
    @CircuitBreaker(name = "items", fallbackMethod="getFallBackMethod")
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> details2(@PathVariable long id) {
        Optional<ItemDto> item = service.findById(id);
        return item.isPresent() ? 
            ResponseEntity.ok(item.get()) : 
            ResponseEntity.notFound().build();
    }
    
    //es necesario para el camino alternativo de @CircuitBreaker    
    public ResponseEntity<?> getFallBackMethod(Throwable e){
    	log.error("Error al consultar el item", e.getMessage());
		Product product = GlobalProd;
		product.setId(1l);
		return ResponseEntity.ok(new ItemDto(product, 5));    	
    }
    
    
    //con esta anotacion le indicamos que la configuracion de CB 
    //a tomar es la hecha en application.properties o application.yml
    @TimeLimiter(name = "items", fallbackMethod = "getFallBackMethod2")
    @GetMapping("/detail2/{id}")
    public CompletableFuture<?> details4(@PathVariable long id) {
    	return CompletableFuture.supplyAsync(()->{
            Optional<ItemDto> item = service.findById(id);
            return item.isPresent() ? 
                ResponseEntity.ok(item.get()) : 
                ResponseEntity.notFound().build();
    	});
    }
    
    //es necesario para el camino alternativo de @TimeLimiter   
    public CompletableFuture<?> getFallBackMethod2(Throwable e){
    	return CompletableFuture.supplyAsync(()->{        	
        	log.error("Error al consultar el item", e.getMessage());
    		Product product = GlobalProd;
    		product.setId(1l);
    		return ResponseEntity.ok(new ItemDto(product, 5));    
    	});	
    }
    
    //con esta anotacion le indicamos que la configuracion de CB 
    //a tomar es la hecha en application.properties o application.yml
    //el fallback de timelimiter se obvia cuando se combinan las 2 anotaciones
    @CircuitBreaker(name = "items", fallbackMethod="getFallBackMethod")
    @TimeLimiter(name = "items")
    @GetMapping("/detail3/{id}")
    public CompletableFuture<?> details3(@PathVariable long id) {
    	return CompletableFuture.supplyAsync(()->{
            Optional<ItemDto> item = service.findById(id);
            return item.isPresent() ? 
                ResponseEntity.ok(item.get()) : 
                ResponseEntity.notFound().build();
    	});
    }
    
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Product product){
    	log.info("Creando Producto: {}", product);
    	Optional saveOp = Optional.ofNullable(this.service.save(product));
    	return saveOp.isPresent()?
    			ResponseEntity.status(HttpStatus.CREATED).body(saveOp.orElseThrow()):
    			ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product product){
    	log.info("Actualizando Producto: {}", product);
    	Optional<ItemDto> item = service.findById(id);
    	return item.map(itm->{
    		Product prod = new Product(id, product.getName(), product.getPrice(), 
    				product.getCreatedAt(), itm.getProduct().getPort());
    		return ResponseEntity.status(HttpStatus.CREATED).body(service.update(prod, id));
    	}).orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
    	log.info("Eliminando Producto");
    	return service.findById(id) 
			.map(product -> { 
		    	service.delete(id);
		    	return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); 
	    	}) 
	    	.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());  		
    }

}
