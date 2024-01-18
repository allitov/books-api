package com.allitov.booksapi.model.service.impl;

import com.allitov.booksapi.model.data.Book;
import com.allitov.booksapi.model.data.Category;
import com.allitov.booksapi.model.repository.BookRepository;
import com.allitov.booksapi.model.repository.CategoryRepository;
import com.allitov.booksapi.model.service.BookService;
import com.allitov.booksapi.util.BeanUtils;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DatabaseBookService implements BookService {

    private final BookRepository bookRepository;

    private final CategoryRepository categoryRepository;

    public Book findBookById(@NonNull Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format(
                        "Book with id {0} not found", id)));
    }

    @Override
    public Book findBookByNameAndAuthor(@NonNull String bookName, @NonNull String author) {
        return bookRepository.findBookByNameAndAuthor(bookName, author)
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format(
                        "Book with name {0} and author {1} not found", bookName, author)));
    }

    @Override
    public List<Book> findBooksByCategoryName(String categoryName) {
        return bookRepository.findBooksByCategoryName(categoryName);
    }

    @Override
    @Transactional
    public Book createBook(@NonNull Book book, @NonNull String categoryName) {
        Optional<Category> category = categoryRepository.findCategoryByName(categoryName);
        book.setCategory(category.orElseGet(() ->
                categoryRepository.save(Category.builder().name(categoryName).build())));

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateBookById(@NonNull Long id, @NonNull Book book, @NonNull String categoryName) {
        Book foundBook = findBookById(id);

        Optional<Category> category = categoryRepository.findCategoryByName(categoryName);
        book.setCategory(category.orElseGet(() ->
                categoryRepository.save(Category.builder().name(categoryName).build())));

        BeanUtils.copyNonNullProperties(book, foundBook);

        return bookRepository.save(foundBook);
    }

    @Override
    public void deleteBookById(@NonNull Long id) {
        bookRepository.deleteById(id);
    }
}
