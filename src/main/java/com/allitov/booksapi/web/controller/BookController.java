package com.allitov.booksapi.web.controller;

import com.allitov.booksapi.model.service.BookService;
import com.allitov.booksapi.web.dto.request.BookRequest;
import com.allitov.booksapi.web.dto.response.BookListResponse;
import com.allitov.booksapi.web.dto.response.BookResponse;
import com.allitov.booksapi.web.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final BookMapper bookMapper;

    @GetMapping
    public ResponseEntity<BookResponse> getBookByNameAndAuthor(@RequestParam(name = "name") String bookName,
                                                               @RequestParam(name = "author") String author) {
        return ResponseEntity.ok(
                bookMapper.bookToResponse(
                        bookService.findBookByNameAndAuthor(bookName, author)
                )
        );
    }

    @GetMapping("/category")
    public ResponseEntity<BookListResponse> getBooksByCategoryName(@RequestParam("name") String categoryName) {
        return ResponseEntity.ok(
                bookMapper.bookListToBookListResponse(
                        bookService.findBooksByCategoryName(categoryName)
                )
        );
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody BookRequest request) {
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
                                               @RequestBody BookRequest request) {
        bookService.updateBook(bookMapper.requestToBook(id, request));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable("id") Long id) {
        bookService.deleteBookById(id);

        return ResponseEntity.noContent().build();
    }
}
