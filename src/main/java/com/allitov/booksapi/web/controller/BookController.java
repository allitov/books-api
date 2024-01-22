package com.allitov.booksapi.web.controller;

import com.allitov.booksapi.exception.ExceptionMessage;
import com.allitov.booksapi.model.service.BookService;
import com.allitov.booksapi.web.dto.request.BookRequest;
import com.allitov.booksapi.web.dto.response.BookListResponse;
import com.allitov.booksapi.web.dto.response.BookResponse;
import com.allitov.booksapi.web.mapper.BookMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;

    private final BookMapper bookMapper;

    @GetMapping
    public ResponseEntity<BookResponse> getBookByNameAndAuthor(
            @RequestParam("name")
            @NotBlank(message = ExceptionMessage.BLANK_BOOK_NAME)
            @Size(max = 256, message = ExceptionMessage.INVALID_BOOK_NAME_LENGTH) String bookName,
            @RequestParam("author")
            @NotBlank(message = ExceptionMessage.BLANK_AUTHOR_NAME)
            @Size(max = 256, message = ExceptionMessage.INVALID_AUTHOR_NAME_LENGTH) String authorName) {
        return ResponseEntity.ok(
                bookMapper.bookToResponse(
                        bookService.findBookByNameAndAuthor(bookName, authorName)
                )
        );
    }

    @GetMapping("/category")
    public ResponseEntity<BookListResponse> getBooksByCategoryName(
            @RequestParam("name")
            @NotBlank(message = ExceptionMessage.BLANK_CATEGORY_NAME)
            @Size(max = 256, message = ExceptionMessage.INVALID_CATEGORY_NAME_LENGTH) String categoryName) {
        return ResponseEntity.ok(
                bookMapper.bookListToBookListResponse(
                        bookService.findBooksByCategoryName(categoryName)
                )
        );
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(bookService.createBook(bookMapper.requestToBook(request)).getId())
                        .toUri()
        ).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBookById(@PathVariable("id") Long id,
                                               @Valid @RequestBody BookRequest request) {
        bookService.updateBook(bookMapper.requestToBook(id, request));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable("id") Long id) {
        bookService.deleteBookById(id);

        return ResponseEntity.noContent().build();
    }
}
