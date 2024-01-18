package com.allitov.booksapi.web.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookRequest {

    private String name;

    private String author;

    private String categoryName;

    private LocalDate publicationDate;
}
