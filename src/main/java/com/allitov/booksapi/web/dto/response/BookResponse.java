package com.allitov.booksapi.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Book Name")
    private String name;

    @Schema(example = "Author Name")
    private String author;

    @Schema(example = "Category Name")
    private String categoryName;

    @Schema(example = "2000-01-01")
    private LocalDate publicationDate;
}
