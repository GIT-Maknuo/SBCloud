package com.sbcloud.msvc.items.models;

import com.sbcloud.commons.entities.Product;

public class ItemDto {

    private Product product;
    private int quantity;

    public ItemDto() {
    }

    public ItemDto(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public Double getTotal() {
        return product.getPrice() * quantity;
    }


}
