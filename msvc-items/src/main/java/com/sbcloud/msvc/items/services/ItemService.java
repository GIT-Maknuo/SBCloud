package com.sbcloud.msvc.items.services;

import java.util.List;
import java.util.Optional;

import com.sbcloud.commons.entities.Product;
import com.sbcloud.msvc.items.models.ItemDto;

public interface ItemService {

    List<ItemDto> findAll();
    
    Optional<ItemDto> findById(Long id);
    
    Product save(Product product);
    
    Product update(Product product, Long id);
    
    void delete(Long id);    

}
