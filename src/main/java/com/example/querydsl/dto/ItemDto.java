package com.example.querydsl.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
// @AllArgsConstructor
@ToString
public class ItemDto {
    private String name;
    private Integer cost;
    private Integer stock;
    private Integer totalCost;

    public ItemDto(String name, Integer cost, Integer stock) {
        this.name = name;
        this.cost = cost;
        this.stock = stock;
        this.totalCost = cost * stock;
    }
}
