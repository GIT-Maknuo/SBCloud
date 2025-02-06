package com.sbcloud.msvc.items.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sbcloud.commons.entities.Product;

@FeignClient(name = "msvc-products")
public interface ProductFeignClient {


    @GetMapping
    public List<Product> findAll();

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id);
    
    @PostMapping
    public Product create(@RequestBody Product product);
    
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product);
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id);


}
