package com.allitov.booksapi.web.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookResponse {

    private Long id;

    private String name;

    private String author;

    private String categoryName;

    private LocalDate publicationDate;
}
