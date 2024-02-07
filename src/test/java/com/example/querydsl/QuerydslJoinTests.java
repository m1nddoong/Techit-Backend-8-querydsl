package com.example.querydsl;

import static com.example.querydsl.entity.QItem.item;
import static com.example.querydsl.entity.QShop.shop;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.querydsl.entity.Item;
import com.example.querydsl.entity.Shop;
import com.example.querydsl.repo.ItemRepository;
import com.example.querydsl.repo.ShopRepository;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import java.time.LocalDateTime;
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
public class QuerydslJoinTests {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private JPAQueryFactory queryFactory;
    @Autowired
    private EntityManagerFactory managerFactory;
    private PersistenceUnitUtil unitUtil;

    // @BeforeEach: 각 테스트 전에 실행할 코드를 작성하는 영역
    @BeforeEach
    public void beforeEach() {
        unitUtil = managerFactory.getPersistenceUnitUtil();
//        Item temp = Item.builder().build();
//        unitUtil.isLoaded(temp.getShop());
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
    public void regularJoins() {
        List<Item> foundList = queryFactory
                .selectFrom(item)
                // INNER JOIN
                .join(item.shop)
                // LEFT JOIN
//                .leftJoin(item.shop)
                // RIGHT JOIN
//                .rightJoin(item.shop)
                .fetch();

        for (Item found: foundList) {
            System.out.println(found);
        }
    }

    @Autowired
    private EntityManager entityManager;

    @Test
    public void fetchJoin() {
        // 영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();

        // 그냥 join은 연관 데이터를 불러오지는 않는다.
        Item found = queryFactory
                .selectFrom(item)
                .join(item.shop)
                .where(item.name.eq("itemA"))
                .fetchOne();
        // 검색한 데이터의 Shop 데이터는 가져와지지 않은 상태
        assertFalse(unitUtil.isLoaded(found.getShop()));

        found = queryFactory
                .selectFrom(item)
                .join(item.shop)
                // Fetch Join으로 변경
                .fetchJoin()
                .where(item.name.eq("itemB"))
                .fetchOne();
        // 검색한 데이터의 Shop 데이터는 가져와진 상태
        assertTrue(unitUtil.isLoaded(found.getShop()));
    }

    /**
     * SELECT COUNT(item), MAX(item.price) FROM Item item;
     */
    @Test
    public void aggregate() {
        // Querydsl의 Tuple
        Tuple result = queryFactory
                // 집계하고 싶은 속성의 집계함수 메서드를 호출하여 select에 추가
                // item.(속성).(집계함수)()
                .select(
                        item.count(),
                        item.price.avg(),
                        item.price.max(),
                        item.stock.sum()
                )
                .from(item)
                .fetchOne();
        // Tuple은 조회했던 QType의 속성 및 집계를 기준으로 데이터 회수 가능
        assertEquals(6, result.get(item.count()));
        assertEquals(175, result.get(item.stock.sum()));
        // 없는건 null로 반환
        assertNull(result.get(item.stock));

        List<Tuple> results = queryFactory
                .select(
                        shop.name,
                        item.count(),
                        item.price.avg(),
                        item.stock.sum()
                )
                .from(item)
                .join(item.shop)
                .groupBy(shop.name)
                .fetch();

        for (Tuple tuple: results) {
            System.out.printf(
                    "%s: %d, %.2f (%d)%n",
                    tuple.get(shop.name),
                    tuple.get(item.count()),
                    tuple.get(item.price.avg()),
                    tuple.get(item.stock.sum())
            );
        }
    }
}