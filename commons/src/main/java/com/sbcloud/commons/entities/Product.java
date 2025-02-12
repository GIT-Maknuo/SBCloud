package com.sbcloud.commons.entities;

import java.time.LocalDate;

import lombok.*;
import jakarta.persistence.*;


@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
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

   
}
