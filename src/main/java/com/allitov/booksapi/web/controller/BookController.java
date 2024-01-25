package com.allitov.booksapi.web.controller;

import com.allitov.booksapi.exception.ExceptionMessage;
import com.allitov.booksapi.model.service.BookService;
import com.allitov.booksapi.web.dto.request.BookRequest;
import com.allitov.booksapi.web.dto.response.BookListResponse;
import com.allitov.booksapi.web.dto.response.BookResponse;
import com.allitov.booksapi.web.dto.response.ErrorResponse;
import com.allitov.booksapi.web.mapper.BookMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Get book by name and author",
            description = "Get book by name and author. Returns a book matching the request parameters",
            parameters = {
                    @Parameter(name = "name", example = "Pin..."),
                    @Parameter(name = "author", example = "Oralle Tarbin")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 200 and book data if everything is successful",
                    responseCode = "200",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = BookResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 400 and error message if request parameters have invalid values",
                    responseCode = "400",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 404 and error message if book was not found",
                    responseCode = "404",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            )
    })
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

    @Operation(
            summary = "Get books by category name",
            description = "Get books by category name. Returns a list of books matching the request parameter",
            parameters = {
                    @Parameter(name = "name", example = "Western")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 200 and books list if everything is successful",
                    responseCode = "200",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = BookListResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 400 and error message if request parameter has invalid value",
                    responseCode = "400",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            )
    })
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

    @Operation(
            summary = "Create book",
            description = "Create book. Returns created book location"
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 201 and created book location if everything is successful",
                    responseCode = "201",
                    headers = {
                            @Header(name = "Location", description = "Created book location")
                    }
            ),
            @ApiResponse(
                    description = "Returns status 400 and error message if request has invalid values",
                    responseCode = "400",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            )
    })
    @PostMapping
    public ResponseEntity<Void> createBook(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(bookService.createBook(bookMapper.requestToBook(request)).getId())
                        .toUri()
        ).build();
    }

    @Operation(
            summary = "Update book by ID",
            description = "Update book by ID. Returns status 'no content'",
            parameters = {
                    @Parameter(name = "id", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 204 if everything is successful",
                    responseCode = "204"
            ),
            @ApiResponse(
                    description = "Returns status 400 and error message if request has invalid values",
                    responseCode = "400",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    description = "Returns status 404 and error message if book with requested ID was not found",
                    responseCode = "404",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    mediaType = "application/json"
                            )
                    }
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBookById(@PathVariable("id") Long id,
                                               @Valid @RequestBody BookRequest request) {
        bookService.updateBook(bookMapper.requestToBook(id, request));

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete book by ID",
            description = "Delete book by ID. Returns status 'no content'",
            parameters = {
                    @Parameter(name = "id", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Returns status 204 if everything id successful",
                    responseCode = "204"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable("id") Long id) {
        bookService.deleteBookById(id);

        return ResponseEntity.noContent().build();
    }
}
