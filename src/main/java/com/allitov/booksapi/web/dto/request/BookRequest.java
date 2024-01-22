package com.allitov.booksapi.web.dto.request;

import com.allitov.booksapi.exception.ExceptionMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequest {

    @NotBlank(message = ExceptionMessage.BLANK_BOOK_NAME)
    @Size(max = 256, message = ExceptionMessage.INVALID_BOOK_NAME_LENGTH)
    private String name;

    @NotBlank(message = ExceptionMessage.BLANK_AUTHOR_NAME)
    @Size(max = 256, message = ExceptionMessage.INVALID_AUTHOR_NAME_LENGTH)
    private String author;

    @NotBlank(message = ExceptionMessage.BLANK_CATEGORY_NAME)
    @Size(max = 256, message = ExceptionMessage.INVALID_CATEGORY_NAME_LENGTH)
    private String categoryName;

    @NotNull(message = ExceptionMessage.BLANK_PUBLICATION_DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;
}
