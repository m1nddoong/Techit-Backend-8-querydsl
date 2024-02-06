package com.example.querydsl.repo;

import com.example.querydsl.entity.Item;
import com.example.querydsl.entity.QItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.AbstractAuditable_;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class QueryDslRepo {
    // Query를 만들기 위한 빌더
    /**
     * ==== SELECT 문 (Statement) ====
     * SELECT i              -- SELECT 절 (Clause)
     * FROM Item i           -- FROM 절 (Clause)
     * WHERE i.name = :name  -- WHERE 절 (Cluase)
     */
    private final JPAQueryFactory queryFactory; // sql 빌더같은 역할\
    private final ItemRepository itemRepository;

    /*public QueryDslRepo(EntityManager entityManager) {
        queryFactory = new JPAQueryFactory(entityManager);
    }*/

    // 새 아이템 만들고 조회하기
    public void helloQuerydsl() {
        itemRepository.save(Item.builder()
                .name("new item")
                .price(1000)
                .stock(1000)
                .build());

        // QItem 은 엔티티 그리고 엔티티가 가질 수 있는 속성을 나타낸다.
        QItem qItem = new QItem("item");

        // 얘는 메서드로서 (자바코드) 호출하고 있다. (Querydsl)
        List<Item> items = queryFactory
                // SELECT 절을 추가
                .select(qItem)
                // FROM 절을 추가
                .from(qItem)
                // WHERE 절을 추가
                .where(qItem.name.eq("new item"))
                // 결과 조회
                .fetch();
        /**
         * // 애는 전부 문자열 (JPQL)
         * String a = """
         *      SELECT i
         *      FROM Item i
         *      WHERE i.name :name
         *      """;
         */


        for (Item item : items) {
            log.info("{}: {} ({})", item.getName(), item.getPrice(), item.getStock());
        }

    }
}
