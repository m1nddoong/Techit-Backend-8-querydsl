package com.example.querydsl;


import com.example.querydsl.repo.QueryDslRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final QueryDslRepo queryDslRepo;

    @GetMapping
    public String test() {
        queryDslRepo.helloQuerydsl();
        return "done";
    }
}
