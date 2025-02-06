package com.sbcloud.msvc.items.services;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sbcloud.msvc.items.clients.ProductFeignClient;
import com.sbcloud.msvc.items.models.ItemDto;
import com.sbcloud.commons.entities.Product;


import feign.FeignException;

@Service
public class ItemServiceFeign implements ItemService {

    private final ProductFeignClient client;

    public ItemServiceFeign(ProductFeignClient productFeignClient) {
        this.client = productFeignClient;
    }

    @Override
    public List<ItemDto> findAll() {
        return client.findAll()
            .stream().map(p -> new ItemDto((Product) p, new Random().nextInt(10)+1))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ItemDto> findById(Long id) {
        try {
            Product product = client.getProduct(id);
            return Optional.of(new ItemDto(product, new Random().nextInt(10)+1));
        } catch (FeignException e) {
            return Optional.empty();
        }
    }

	@Override
	public Product save(Product product) {
		return client.create(product);
	}

	@Override
	public Product update(Product product, Long id) {
		return client.update(id, product);
	}

	@Override
	public void delete(Long id) {
		client.delete(id);
	}

}
