package com.allitov.booksapi.model.service.impl;

import com.allitov.booksapi.model.data.Book;
import com.allitov.booksapi.model.data.Category;
import com.allitov.booksapi.model.repository.BookRepository;
import com.allitov.booksapi.model.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseBookServiceTests {

    private final BookRepository bookRepository = Mockito.mock(BookRepository.class);

    private final CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);

    private final CacheManager cacheManager = Mockito.mock(CacheManager.class);

    private final DatabaseBookService service =
            new DatabaseBookService(bookRepository, categoryRepository, cacheManager);

    @Test
    public void whenFindBookById_thenReturnBook() {
        Long bookId = 10L;
        Book expectedBook = createBookEntity();

        Mockito.when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(expectedBook));

        Book actualBook = service.findBookById(bookId);

        Mockito.verify(bookRepository, Mockito.times(1))
                .findById(bookId);

        Assertions.assertEquals(expectedBook, actualBook, "Returned not expected book.");
    }

    @Test
    public void whenFindBookByWrongId_thenThrowError() {
        Long bookId = 10L;

        Mockito.when(bookRepository.findById(bookId))
                .thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.findBookById(bookId),
                "Expected 'findBookById(Long id)' to throw, but it didn't."
        );

        Mockito.verify(bookRepository, Mockito.times(1))
                .findById(bookId);

        Assertions.assertEquals(
                String.format("Book with id '%d' not found", bookId),
                thrown.getMessage()
        );
    }

    @Test
    public void whenFindBookByNameAndAuthor_thenReturnBook() {
        String bookName = "Book Name";
        String author = "Author Name";
        Book expectedBook = createBookEntity();

        Mockito.when(bookRepository.findFirstBookByNameAndAuthor(bookName, author))
                .thenReturn(Optional.of(expectedBook));

        Book actualBook = service.findBookByNameAndAuthor(bookName, author);

        Mockito.verify(bookRepository, Mockito.times(1))
                .findFirstBookByNameAndAuthor(bookName, author);

        Assertions.assertEquals(expectedBook, actualBook, "Returned not expected book.");
    }

    @Test
    public void whenFindNonexistentBookByNameAndAuthor_thenThrowError() {
        String bookName = "Book Name";
        String author = "Author Name";

        Mockito.when(bookRepository.findFirstBookByNameAndAuthor(bookName, author))
                .thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.findBookByNameAndAuthor(bookName, author),
                "Expected 'findBookByNameAndAuthor(String bookName, String author)' to throw, but it didn't."
        );

        Mockito.verify(bookRepository, Mockito.times(1))
                .findFirstBookByNameAndAuthor(bookName, author);

        Assertions.assertEquals(
                String.format("Book with name '%s' and author '%s' not found", bookName, author),
                thrown.getMessage()
        );
    }

    @Test
    public void whenFindBooksByCategoryName_thenReturnBooks() {
        String categoryName = "Category Name";
        List<Book> expectedBooks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            expectedBooks.add(createBookEntity());
        }

        Mockito.when(bookRepository.findBooksByCategoryName(categoryName))
                .thenReturn(expectedBooks);

        List<Book> actualBooks = service.findBooksByCategoryName(categoryName);

        Mockito.verify(bookRepository, Mockito.times(1))
                .findBooksByCategoryName(categoryName);

        Assertions.assertEquals(expectedBooks, actualBooks, "Returned not expected books.");
    }

    @Test
    public void whenCreateBook_thenReturnCreatedBook() {
        String categoryName = "Category Name";
        Category expectedCategory = createCategoryEntity();
        Book expectedBook = createBookEntity();

        Mockito.when(categoryRepository.findCategoryByName(categoryName))
                .thenReturn(Optional.of(expectedCategory));
        Mockito.when(bookRepository.save(expectedBook))
                .thenReturn(expectedBook);

        Book actualBook = service.createBook(expectedBook);

        Mockito.verify(categoryRepository, Mockito.times(1))
                .findCategoryByName(categoryName);
        Mockito.verify(bookRepository, Mockito.times(1))
                .save(expectedBook);

        Assertions.assertEquals(expectedBook, actualBook, "Returned not expected book.");
    }

    @Test
    public void whenCreateBookWithNewCategory_thenReturnCreatedBook() {
        String categoryName = "Category Name";
        Category newCategory = createCategoryEntity();
        newCategory.setId(null);
        Category createdCategory = createCategoryEntity();
        Book expectedBook = createBookEntity();

        Mockito.when(categoryRepository.findCategoryByName(categoryName)).thenReturn(Optional.empty());
        Mockito.when(categoryRepository.save(newCategory)).thenReturn(createdCategory);
        Mockito.when(bookRepository.save(expectedBook)).thenReturn(expectedBook);

        Book actualBook = service.createBook(expectedBook);

        Mockito.verify(categoryRepository, Mockito.times(1)).findCategoryByName(categoryName);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(newCategory);
        Mockito.verify(bookRepository, Mockito.times(1)).save(expectedBook);

        Assertions.assertEquals(expectedBook, actualBook, "Returned not expected book.");
    }

    @Test
    public void whenUpdateBookById_thenReturnUpdatedBook() {
        Long bookId = 10L;
        String categoryName = "Category Name";
        Category category = createCategoryEntity();
        Book expectedBook = createBookEntity();

        Mockito.when(categoryRepository.findCategoryByName(categoryName))
                .thenReturn(Optional.of(category));
        Mockito.when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(expectedBook));
        Mockito.when(bookRepository.save(expectedBook))
                .thenReturn(expectedBook);

        Book actualBook = service.updateBook(expectedBook);

        Mockito.verify(categoryRepository, Mockito.times(1))
                .findCategoryByName(categoryName);
        Mockito.verify(bookRepository, Mockito.times(1))
                .findById(bookId);
        Mockito.verify(bookRepository, Mockito.times(1))
                .save(expectedBook);

        Assertions.assertEquals(expectedBook, actualBook, "Returned not expected book.");
    }

    @Test
    public void whenUpdateBookByIdWithNewCategory_thenReturnBook() {
        Long bookId = 10L;
        String categoryName = "Category Name";
        Category newCategory = createCategoryEntity();
        newCategory.setId(null);
        Category createdCategory = createCategoryEntity();
        Book expectedBook = createBookEntity();

        Mockito.when(categoryRepository.findCategoryByName(categoryName))
                .thenReturn(Optional.empty());
        Mockito.when(categoryRepository.save(newCategory))
                .thenReturn(createdCategory);
        Mockito.when(bookRepository.save(expectedBook))
                .thenReturn(expectedBook);
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(expectedBook));

        Book actualBook = service.updateBook(expectedBook);

        Mockito.verify(categoryRepository, Mockito.times(1))
                .findCategoryByName(categoryName);
        Mockito.verify(categoryRepository, Mockito.times(1))
                .save(newCategory);
        Mockito.verify(bookRepository, Mockito.times(1))
                .save(expectedBook);
        Mockito.verify(bookRepository, Mockito.times(1))
                .findById(bookId);

        Assertions.assertEquals(expectedBook, actualBook, "Returned not expected book.");
    }

    @Test
    public void whenDeleteBookById_thenReturnVoid() {
        Long bookId = 10L;

        service.deleteBookById(bookId);

        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(bookId);
    }

    private Book createBookEntity() {
        return Book.builder()
                .id(10L)
                .name("Book Name")
                .author("Author Name")
                .category(createCategoryEntity())
                .publicationDate(LocalDate.of(1990, 1, 1))
                .build();
    }

    private Category createCategoryEntity() {
        return Category.builder()
                .id(50L)
                .name("Category Name")
                .build();
    }
}
