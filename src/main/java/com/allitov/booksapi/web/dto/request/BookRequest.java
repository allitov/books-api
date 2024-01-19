package com.allitov.booksapi.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequest {

    private String name;

    private String author;

    private String categoryName;

    private LocalDate publicationDate;
}
