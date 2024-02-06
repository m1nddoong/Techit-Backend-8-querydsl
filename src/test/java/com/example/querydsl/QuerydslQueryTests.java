package com.example.querydsl;

import static com.example.querydsl.entity.QItem.item;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.querydsl.entity.Item;
import com.example.querydsl.entity.QItem;
import com.example.querydsl.entity.Shop;
import com.example.querydsl.repo.ItemRepository;
import com.example.querydsl.repo.ShopRepository;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
public class QuerydslQueryTests {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private JPAQueryFactory queryFactory;

    // @BeforeEach: 각 테스트 전에 실행할 코드를 작성하는 영역
    @BeforeEach
    public void beforeEach() {
        // shop 2개 item 6개 테스트 용 데이터
        Shop shopA = shopRepository.save(Shop.builder()
                .name("shopA")
                .description("shop A description")
                .build());
        Shop shopB = shopRepository.save(Shop.builder()
                .name("shopB")
                .description("shop B description")
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
                        .name("itemE")
                        .price(7500)
                        .stock(25)
                        .build()
        ));
    }


    // 가지고 오는 방법에 대한 이야기
    @Test
    public void fetch() {
        // fetch() : 단순하게 전체 조회
        List<Item> foundList = queryFactory
                // SELECT FROM 절
                .selectFrom(item)
                // 결과를 전체 리스트 형태로 조회
                .fetch();
        assertEquals(6, foundList.size());

        // fetchOne() : 하나만 조회
        Item found = queryFactory
                .selectFrom(item)
                .where(item.id.eq(1L))
                // 하나만 조회
                .fetchOne();
        assertEquals(1L, found.getId());

        found = queryFactory
                .selectFrom(item)
                .where(item.id.eq(0L))
                // 없을 경우 null
                .fetchOne();
        assertNull(found);

        assertThrows(Exception.class, () -> {
            queryFactory.selectFrom(item)
                    // 2개 이상일 경우 Exception
                    .fetchOne();
        });

        // fetchFirst() : 첫번쨰 결과 또는 Null
        found = queryFactory
                .selectFrom(item)
                // LIMIT 1 -> fetchOne();
                // 제일 앞에 있는 것 또는 null 반환
                .fetchFirst();
        assertNotNull(found);

        // 페이지 네이션
        // offset limit
        foundList = queryFactory
                .selectFrom(item)
                // offset 이 3 일떄 3번 까지 스킵하고 4번 부터 시작 -> 4 5
                .offset(3)
                .limit(2)
                .fetch();
        for (Item find : foundList) {
            System.out.println(find.getId());
        }

        // fetchCount() : 결과의 갯수 반환 (deprecated)
        long count = queryFactory
                .selectFrom(item)
                .fetchCount();
        assertEquals(6, count);

        // fetchResult() : 결과 및 count + offset + limit 정보 반환 (deprecated)
        QueryResults<Item> results = queryFactory
                .selectFrom(item)
                .offset(3)
                .limit(2)
                .fetchResults();
        System.out.println(results.getTotal());
        System.out.println(results.getOffset());
        System.out.println(results.getLimit());
        // 실제 내용은 getResults()
        foundList = results.getResults();
    }

    // 결과가 있으니 그 결과에 대한 정렬 이야기
    @Test
    public void sort() {
        itemRepository.saveAll(List.of(
                Item.builder()
                        .name("itemF")
                        .price(6000)
                        .stock(40)
                        .build(),
                Item.builder()
                        .price(6000)
                        .stock(40)
                        .build()
        ));
        List<Item> foundList = queryFactory
                // SELECT i FROM Item i
                .selectFrom(item)
                // item.(속성).(순서)를 ORDER BY 넣을 순서대로
                // ORDER BY i.price asc
                .orderBy(
                        // item.price asc
                        // 아이템이 가지고 있는 가격 이라는 속성이 오름차순으로 정렬이 될 것이다.
                        item.price.asc(),
                        item.stock.desc(),
                        // null 이 먼저냐 나중이냐
                        item.name.asc().nullsLast()
                        // item.name.asc().nullsFirst()
                )
                .fetch();

        for (Item found : foundList) {
            System.out.printf("%s: %d (%d)%n", found.getName(), found.getPrice(), found.getStock());
        }
    }

}




















