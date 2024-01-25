package com.allitov.booksapi.model.service;

import com.allitov.booksapi.model.data.Book;

import java.util.List;

public interface BookService {

    Book findBookByNameAndAuthor(String bookName, String author);

    List<Book> findBooksByCategoryName(String categoryName);

    Book createBook(Book book);

    Book updateBook(Book book);

    void deleteBookById(Long id);
}
