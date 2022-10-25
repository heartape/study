package com.heartape.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Goods {
    private Integer id;
    private BigDecimal price;
    private Integer stock;
}
