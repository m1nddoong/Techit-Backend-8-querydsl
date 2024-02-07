package com.example.querydsl;

import static com.example.querydsl.entity.QItem.item;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.querydsl.entity.Item;
import com.example.querydsl.entity.Shop;
import com.example.querydsl.repo.ItemRepository;
import com.example.querydsl.repo.ShopRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class QuerydslDynamicQueryTests {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private JPAQueryFactory queryFactory;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private EntityManagerFactory managerFactory;
    private PersistenceUnitUtil unitUtil;

    // @BeforeEach: 각 테스트 전에 실행할 코드를 작성하는 영역
    @BeforeEach
    public void beforeEach() {
        unitUtil = managerFactory.getPersistenceUnitUtil();
        Shop shopA = shopRepository.save(Shop.builder()
                .name("shopA")
                .description("shop A description")
                .build());
        Shop shopB = shopRepository.save(Shop.builder()
                .name("shopB")
                .description("shop B description")
                .build());
        shopRepository.save(Shop.builder()
                .name("shopC")
                .description("shop C description")
                .build());

        itemRepository.saveAll(List.of(
                Item.builder()
                        .shop(shopA)
                        .name("itemA")
                        .price(5000)
                        .stock(20)
                        .build(),
                Item.builder()
                        .shop(shopA)
                        .name("itemB")
                        .price(6000)
                        .stock(30)
                        .build(),
                Item.builder()
                        .shop(shopB)
                        .name("itemC")
                        .price(8000)
                        .stock(40)
                        .build(),
                Item.builder()
                        .shop(shopB)
                        .name("itemD")
                        .price(10000)
                        .stock(50)
                        .build(),
                Item.builder()
                        .name("itemE")
                        .price(5500)
                        .stock(10)
                        .build(),
                Item.builder()
                        .price(7500)
                        .stock(25)
                        .build()
        ));
    }

    @Test
    public void dynamicQueryTester() {
        List<Item> results = null;
        String name = "itemA";
        Integer price = 5000;
        Integer stock = 20;

        results = booleanBuilder(name, price, stock);
        results.forEach(System.out::println);

        results = booleanBuilder(name, null, null);
        results.forEach(System.out::println);

        results = booleanBuilder(null, null, stock);
        results.forEach(System.out::println);

        results = booleanBuilder(null, price, stock);
        results.forEach(System.out::println);

        results = booleanBuilder(null, null, null);
        results.forEach(System.out::println);
    }

    public List<Item> booleanBuilder(
            String name,
            Integer price,
            Integer stock
    ) {
        // 1. BooleanBuilder : 여러 조건을 엮어서 하나의 조건으로 만들어진
        //                      BooleanBuilder를 사용하는 방법
        //                      생성자에 초기 조건 생성 가능
        BooleanBuilder booleanBuilder = new BooleanBuilder(item.name.isNotNull());
        // 여태까지 누적된 조건에 대하여, 주어진 조건을 AND로 엮는다.
        if (name != null)
            // (여태까지의 조건) AND i.name = name
            booleanBuilder.and(item.name.eq(name));
        if (price != null)
            // (여태까지의 조건) AND i.price = price
            booleanBuilder.and(item.price.eq(price));
        if (stock != null)
            // (여태까지의 조건) AND i.stock = stock
            booleanBuilder.and(item.stock.eq(stock));
        // i.name = name AND i.price = price AND i.stock = stock

        return queryFactory
                .selectFrom(item)
                .where(booleanBuilder)
                .fetch();
    }
}