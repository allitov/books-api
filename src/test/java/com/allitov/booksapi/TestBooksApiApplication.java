package com.allitov.booksapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestBooksApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(BooksApiApplication::main).with(TestBooksApiApplication.class).run(args);
    }

}
