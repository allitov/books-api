package com.allitov.booksapi.model.service.impl;

import com.allitov.booksapi.exception.ExceptionMessage;
import com.allitov.booksapi.model.data.Book;
import com.allitov.booksapi.model.data.Category;
import com.allitov.booksapi.model.repository.BookRepository;
import com.allitov.booksapi.model.repository.CategoryRepository;
import com.allitov.booksapi.model.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "redisCacheManager")
public class DatabaseBookService implements BookService {

    private final BookRepository bookRepository;

    private final CategoryRepository categoryRepository;

    private final CacheManager cacheManager;

    public Book findBookById(@NonNull Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ExceptionMessage.BOOK_BY_ID_NOT_FOUND, id)));
    }

    @Override
    @Cacheable(value = "bookByNameAndAuthor", key = "#bookName.concat('-').concat(#author)")
    public Book findBookByNameAndAuthor(@NonNull String bookName, @NonNull String author) {
        return bookRepository.findFirstBookByNameAndAuthor(bookName, author)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ExceptionMessage.BOOK_BY_NAME_AND_AUTHOR_NOT_FOUND, bookName, author)));
    }

    @Override
    @Cacheable(value = "booksByCategoryName", key = "#categoryName")
    public List<Book> findBooksByCategoryName(String categoryName) {
        return bookRepository.findBooksByCategoryName(categoryName);
    }

    @Override
    @Transactional
    @CacheEvict(value = "booksByCategoryName", key = "#book.category.name", beforeInvocation = true)
    public Book createBook(@NonNull Book book) {
        String categoryName = book.getCategory().getName();

        Optional<Category> category = categoryRepository.findCategoryByName(categoryName);
        book.setCategory(category.orElseGet(() ->
                categoryRepository.save(Category.builder().name(categoryName).build())));

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(
                    value = "bookByNameAndAuthor",
                    key = "#book.name.concat('-').concat(#book.author)",
                    beforeInvocation = true
            ),
            @CacheEvict(
                    value = "booksByCategoryName",
                    key = "#book.category.name",
                    beforeInvocation = true
            )
    })
    public Book updateBook(@NonNull Book book) {
        findBookById(book.getId());
        String categoryName = book.getCategory().getName();

        Optional<Category> category = categoryRepository.findCategoryByName(categoryName);
        book.setCategory(category.orElseGet(() ->
                categoryRepository.save(Category.builder().name(categoryName).build())));

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteBookById(@NonNull Long id) {
        List<Book> deletedBooks = bookRepository.deleteBookById(id);

        if (!deletedBooks.isEmpty()) {
            Book book = deletedBooks.getFirst();
            String bookByNameAndAuthorKey = book.getName() + "-" + book.getAuthor();
            String booksByCategoryNameKey = book.getCategory().getName();
            cacheManager.getCache("bookByNameAndAuthor").evictIfPresent(bookByNameAndAuthorKey);
            cacheManager.getCache("booksByCategoryName").evictIfPresent(booksByCategoryNameKey);
        }
    }
}
