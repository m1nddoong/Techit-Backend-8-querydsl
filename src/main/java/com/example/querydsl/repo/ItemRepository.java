package com.example.querydsl.repo;

import com.example.querydsl.entity.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Intellij가 여기에 들어가 문자열은 JPQL임을 안다.
    @Query("SELECT i FROM Item i")
    List<Item> findWithJpql();

}
