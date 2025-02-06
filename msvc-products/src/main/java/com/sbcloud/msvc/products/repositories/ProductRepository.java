package com.sbcloud.msvc.products.repositories;

import org.springframework.data.repository.CrudRepository;

import com.sbcloud.commons.entities.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

    

}
