package com.sbcloud.commons.entities;

import jakarta.persistence.Transient;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double price;

    @Column(name = "create_at")
    private LocalDate createdAt;

    @Transient
    private Integer port;

    public Product() {
    }

    public Product(String name, Double price, LocalDate createdAt) {
        this.name = name;
        this.price = price;
        this.createdAt = createdAt;
    }
    
    public Product(Long id ,String name, Double price, LocalDate createdAt, Integer port) {
        this.id = id;
    	this.name = name;
        this.price = price;
        this.createdAt = createdAt;
        this.port = port;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }    

}
