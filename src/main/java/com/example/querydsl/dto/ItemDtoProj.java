package com.example.querydsl.dto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
// @AllArgsConstructor
@ToString
public class ItemDtoProj {
    private String name;
    private Integer cost;
    private Integer stock;

    @QueryProjection
    // QDto 를 만들고, 그 생성자를 사용해 데이터를
    // Projection 할 수 있다.
    public ItemDtoProj(
            String name,
            Integer cost,
            Integer stock
    ) {
        this.name = name;
        this.cost = cost;
        this.stock = stock;
    }
    // Querydsl과 DTO의 결합력(Coupling)이 강해진다.
    // Querydsl에 대한 의존도가 높압진다.
}
