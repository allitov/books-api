package com.allitov.booksapi.integration;

import com.allitov.booksapi.model.data.Book;
import com.allitov.booksapi.model.data.Category;
import com.allitov.booksapi.util.TestUtils;
import com.allitov.booksapi.web.dto.request.BookRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Objects;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BooksApiIntegrationTests extends AbstractIntegrationTests {

    @Test
    public void whenGetBookByNameAndAuthor_thenReturnBook() throws Exception {
        String bookName = "Prisoners of the Lost Universe";
        String authorName = "Sisile Erricker";
        String redisKey = "bookByNameAndAuthor::" + bookName + "-" + authorName;

        assertTrue(Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());

        String actualResponse = mockMvc.perform(
                get(String.format("/api/v1/book?name=%s&author=%s", bookName, authorName)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(Objects.requireNonNull(redisTemplate.hasKey(redisKey)));

        String expectedResponse = TestUtils.readStringFromResource(
                "response/integration/get_book_by_name_and_author_response.json");

        assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    public void whenGetNonexistentBookByNameAndAuthor_thenReturnErrorMessage() throws Exception {
        String bookName = "Book Name";
        String authorName = "Author Name";

        assertTrue(Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());

        String actualResponse = mockMvc.perform(
                get(String.format("/api/v1/book?name=%s&author=%s", bookName, authorName)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());

        String expectedResponse = TestUtils.readStringFromResource(
                "response/integration/ger_book_by_name_and_author_error_response.json");

        assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    public void whenGetBooksByCategoryName_thenReturnBooks() throws Exception {
        String categoryName = "Comedy";
        String redisKey = "booksByCategoryName::" + categoryName;

        assertTrue(Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());

        String actualResponse = mockMvc.perform(
                get(String.format("/api/v1/book/category?name=%s", categoryName)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(Objects.requireNonNull(redisTemplate.hasKey(redisKey)));

        String expectedResponse = TestUtils.readStringFromResource(
                "response/integration/get_books_by_category_name_response.json");

        assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    public void whenCreateBook_thenReturnLocation() throws Exception {
        BookRequest request = new BookRequest("New Book", "New Author",
                "New Category", LocalDate.of(2000, 1, 1));

        assertTrue(Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());
        assertEquals(5, bookRepository.count());

        mockMvc.perform(
                post("/api/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", Matchers.containsString("/api/v1/book/6")));

        assertTrue(Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());
        assertEquals(6, bookRepository.count());
    }

    @Test
    public void whenUpdateBookById_thenReturnStatusNoContent() throws Exception {
        Long bookId = 1L;
        Long categoryId = 6L;
        String bookName = "Updated Book";
        String authorName = "Updated Author";
        String categoryName = "Updated Category";
        LocalDate publicationDate = LocalDate.of(2000, 1, 1);
        Category newCategory = new Category(categoryId, categoryName);
        BookRequest request = new BookRequest(bookName, authorName, categoryName, publicationDate);
        Book expectedBook = new Book(bookId, bookName, authorName, newCategory, publicationDate);

        assertTrue(Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());

        mockMvc.perform(
                put("/api/v1/book/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        assertTrue(Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());
        assertEquals(expectedBook, bookRepository.findById(bookId).orElse(null));
    }

    @Test
    public void whenDeleteBookById_thenReturnStatusNoContent() throws Exception {
        Long bookId = 1L;

        assertTrue(Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());
        assertEquals(5, bookRepository.count());

        mockMvc.perform(
                delete("/api/v1/book/{id}", bookId))
                .andExpect(status().isNoContent());

        assertTrue(Objects.requireNonNull(redisTemplate.keys("*")).isEmpty());
        assertEquals(4, bookRepository.count());
    }
}
