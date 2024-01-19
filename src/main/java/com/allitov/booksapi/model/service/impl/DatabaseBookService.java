package com.allitov.booksapi.model.service.impl;

import com.allitov.booksapi.model.data.Book;
import com.allitov.booksapi.model.data.Category;
import com.allitov.booksapi.model.repository.BookRepository;
import com.allitov.booksapi.model.repository.CategoryRepository;
import com.allitov.booksapi.model.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DatabaseBookService implements BookService {

    private final BookRepository bookRepository;

    private final CategoryRepository categoryRepository;

    public Book findBookById(@NonNull Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Book with id '%d' not found", id)));
    }

    @Override
    public Book findBookByNameAndAuthor(@NonNull String bookName, @NonNull String author) {
        return bookRepository.findBookByNameAndAuthor(bookName, author)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Book with name '%s' and author '%s' not found", bookName, author)));
    }

    @Override
    public List<Book> findBooksByCategoryName(String categoryName) {
        return bookRepository.findBooksByCategoryName(categoryName);
    }

    @Override
    @Transactional
    public Book createBook(@NonNull Book book) {
        String categoryName = book.getCategory().getName();

        Optional<Category> category = categoryRepository.findCategoryByName(categoryName);
        book.setCategory(category.orElseGet(() ->
                categoryRepository.save(Category.builder().name(categoryName).build())));

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateBook(@NonNull Book book) {
        findBookById(book.getId());
        String categoryName = book.getCategory().getName();

        Optional<Category> category = categoryRepository.findCategoryByName(categoryName);
        book.setCategory(category.orElseGet(() ->
                categoryRepository.save(Category.builder().name(categoryName).build())));

        return bookRepository.save(book);
    }

    @Override
    public void deleteBookById(@NonNull Long id) {
        bookRepository.deleteById(id);
    }
}
