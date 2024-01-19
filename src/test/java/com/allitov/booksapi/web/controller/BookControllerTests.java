package com.allitov.booksapi.web.controller;

import com.allitov.booksapi.model.data.Book;
import com.allitov.booksapi.model.data.Category;
import com.allitov.booksapi.model.service.BookService;
import com.allitov.booksapi.web.dto.request.BookRequest;
import com.allitov.booksapi.web.dto.response.BookListResponse;
import com.allitov.booksapi.web.dto.response.BookResponse;
import com.allitov.booksapi.web.mapper.BookMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import net.javacrumbs.jsonunit.JsonAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(BookController.class)
public class BookControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookMapper bookMapper;

    @MockBean
    private BookService bookService;

    @Test
    public void whenGetBookByNameAndAuthor_thenReturnBookResponse() throws Exception {
        String bookName = "Book Name";
        String authorName = "Author Name";
        Book foundBook = createBookEntity();
        BookResponse response = createBookResponse(foundBook);

        Mockito.when(bookService.findBookByNameAndAuthor(bookName, authorName))
                .thenReturn(foundBook);
        Mockito.when(bookMapper.bookToResponse(foundBook))
                .thenReturn(response);

        String actualResponse = mockMvc.perform(MockMvcRequestBuilders
                .get(String.format("/api/v1/book?name=%s&author=%s", bookName, authorName)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(bookService, Mockito.times(1))
                .findBookByNameAndAuthor(bookName, authorName);
        Mockito.verify(bookMapper, Mockito.times(1))
                .bookToResponse(foundBook);

        String expectedResponse = readStringFromResource(
                "response/get_book_by_name_and_author_response.json");

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    public void whenGetNonexistentBookByNameAndAuthor_thenReturnErrorMessage() throws Exception {
        String bookName= "Book Name";
        String authorName = "Author Name";
        Mockito.when(bookService.findBookByNameAndAuthor(bookName, authorName))
                .thenThrow(new EntityNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders
                .get(String.format("/api/v1/book?name=%s&author=%s", bookName, authorName)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(bookService, Mockito.times(1))
                .findBookByNameAndAuthor(bookName, authorName);
    }

    @Test
    public void whenGetBooksByCategoryName_thenReturnBooks() throws Exception {
        String categoryName = "Category Name";
        List<Book> foundBooks = new ArrayList<>();
        foundBooks.add(createBookEntity());
        BookListResponse response = new BookListResponse();
        List<BookResponse> bookResponses = new ArrayList<>();
        bookResponses.add(createBookResponse(createBookEntity()));
        response.setBooks(bookResponses);

        Mockito.when(bookService.findBooksByCategoryName(categoryName))
                .thenReturn(foundBooks);
        Mockito.when(bookMapper.bookListToBookListResponse(foundBooks))
                .thenReturn(response);

        String actualResponse = mockMvc.perform(MockMvcRequestBuilders
                .get(String.format("/api/v1/book/category?name=%s", categoryName)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(bookService, Mockito.times(1))
                .findBooksByCategoryName(categoryName);
        Mockito.verify(bookMapper, Mockito.times(1))
                .bookListToBookListResponse(foundBooks);

        String expectedResponse = readStringFromResource(
                "response/get_books_by_category_name_response.json");

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    public void whenCreateBook_thenReturnLocation() throws Exception {
        Long createdBookId = 10L;
        Book book = createBookEntity();
        BookRequest bookRequest = createBookRequest(book);

        Mockito.when(bookMapper.requestToBook(bookRequest))
                .thenReturn(book);
        Mockito.when(bookService.createBook(book))
                .thenReturn(book);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers
                        .header()
                        .string(
                                "Location",
                                Matchers.containsString(String.format("/api/v1/book/%d", createdBookId))
                        )
                );

        Mockito.verify(bookMapper, Mockito.times(1))
                .requestToBook(bookRequest);
        Mockito.verify(bookService, Mockito.times(1))
                .createBook(book);
    }

    @Test
    public void whenUpdateBookById_thenReturnStatusNoContent() throws Exception {
        Long bookId = 10L;
        Book book = createBookEntity();
        BookRequest request = createBookRequest(book);

        Mockito.when(bookMapper.requestToBook(bookId, request)).thenReturn(book);
        Mockito.when(bookService.updateBook(book)).thenReturn(book);

        mockMvc.perform(MockMvcRequestBuilders
                .put(String.format("/api/v1/book/%d", bookId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(bookMapper, Mockito.times(1))
                .requestToBook(bookId, request);
        Mockito.verify(bookService, Mockito.times(1))
                .updateBook(book);
    }

    @Test
    public void whenUpdateBookByNonexistentId_thenReturnErrorMessage() throws Exception {
        Long bookId = 10L;
        Book book = createBookEntity();
        BookRequest request = createBookRequest(book);

        Mockito.when(bookMapper.requestToBook(bookId, request))
                .thenReturn(book);
        Mockito.when(bookService.updateBook(book))
                .thenThrow(new EntityNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders
                .put(String.format("/api/v1/book/%d", bookId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(bookMapper, Mockito.times(1))
                .requestToBook(bookId, request);
        Mockito.verify(bookService, Mockito.times(1))
                .updateBook(book);
    }

    @Test
    public void whenDeleteBookById_thenReturnStatusNoContent() throws Exception {
        Long bookId = 10L;

        mockMvc.perform(MockMvcRequestBuilders
                .delete(String.format("/api/v1/book/%d", bookId)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(bookService, Mockito.times(1))
                .deleteBookById(bookId);
    }

    private String readStringFromResource(String resourcePath) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(
                MessageFormat.format("classpath:{0}", resourcePath)
        );

        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Book createBookEntity() {
        return Book.builder()
                .id(10L)
                .name("Book Name")
                .author("Author Name")
                .category(createCategoryEntity())
                .publicationDate(LocalDate.of(2012, 12, 12))
                .build();
    }

    private Category createCategoryEntity() {
        return Category.builder()
                .id(10L)
                .name("Category Name")
                .build();
    }

    private BookResponse createBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .name(book.getName())
                .author(book.getAuthor())
                .categoryName(book.getCategory().getName())
                .publicationDate(book.getPublicationDate())
                .build();
    }

    private BookRequest createBookRequest(Book book) {
        return BookRequest.builder()
                .name(book.getName())
                .author(book.getAuthor())
                .categoryName(book.getCategory().getName())
                .publicationDate(book.getPublicationDate())
                .build();
    }
}
