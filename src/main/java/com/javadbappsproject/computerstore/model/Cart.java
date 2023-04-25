package com.javadbappsproject.computerstore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Cart {

    private Long id;
    private String name;
    private BigDecimal price;
    private int quantity;
    private String image;
}
