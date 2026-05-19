package com.preethisri.retailapp.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,length = 100)
    private String productName;

    @Column(precision = 10,scale = 2)
    private BigDecimal price;

    private String description;

    @Column(nullable = false)
    private Integer stock;

    @Column(length = 20)
    private String category;
}
