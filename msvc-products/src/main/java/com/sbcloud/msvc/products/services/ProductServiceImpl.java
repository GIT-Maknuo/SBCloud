package com.sbcloud.msvc.products.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbcloud.commons.entities.Product;
import com.sbcloud.msvc.products.repositories.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final Environment env;

    public ProductServiceImpl(ProductRepository productRepository , Environment env) {
        this.env = env;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return ((List<Product>) productRepository.findAll()).stream().map(product -> {
            product.setPort(Integer.parseInt(env.getProperty("local.server.port")));
            return product;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id).map(product -> {
            product.setPort(Integer.parseInt(env.getProperty("local.server.port")));
            return product;
        });
    }

	@Override
	@Transactional
	public Product save(Product product) {
		return this.productRepository.save(product);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		this.productRepository.deleteById(id);		
	}

}
