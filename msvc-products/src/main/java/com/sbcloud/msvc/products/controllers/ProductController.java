package com.sbcloud.msvc.products.controllers;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sbcloud.commons.entities.Product;
import com.sbcloud.msvc.products.services.ProductService;

@RestController
public class ProductController {
	
	private final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService service;

    public ProductController(ProductService productService) {
        this.service = productService;
    }   
    
    @GetMapping
    public ResponseEntity<?> getProduct() {
    	log.info("Metodo controller::getProduct()");
        return service.findAll().isEmpty() ? 
            ResponseEntity.notFound().build() :  
            ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProducts(@PathVariable Long id) throws InterruptedException {
    	log.info("Metodo controller::getProducts()");

    	//casos para probar el circuit breaker
    	if (id == 40l) {
			throw new IllegalStateException("This is a test exception");
		}
    	if (id == 7l) {
    		TimeUnit.SECONDS.sleep(3l);
    	}    	
    	//fin casos para probar el circuit breaker    	
    	
        Optional<Product> prod = service.findById(id);
        return service.findById(id).isPresent() ? 
            ResponseEntity.ok(prod.orElseThrow()) :
            ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Product product){
    	log.info("Metodo controller::create(), creando:{}", product);
    	return Optional.ofNullable(service.save(product)).isPresent()?
            ResponseEntity.status(HttpStatus.CREATED).body(product) :
            ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product product){
    	log.info("Metodo controller::update(), actualizando:{}", product);
    	return service.findById(id) 
			.map(productDB -> { 
    				productDB = new Product(id, product.getName(), product.getPrice(), LocalDate.now(),productDB.getPort()); 
    				return ResponseEntity.status(HttpStatus.CREATED).body(service.save(productDB)); 
				}) 
			.orElse(ResponseEntity.notFound().build()); 
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
    	log.info("Metodo controller::delete()");
    	return service.findById(id) 
			.map(product -> { 
		    		service.deleteById(id); 
		    		return ResponseEntity.noContent().build(); 
	    		}) 
	    	.orElse(ResponseEntity.notFound().build());  		
    }

}
