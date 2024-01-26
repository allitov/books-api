package com.allitov.booksapi.model.repository;

import com.allitov.booksapi.model.data.Book;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findFirstBookByNameAndAuthor(String bookName, String author);

    List<Book> findBooksByCategoryName(String categoryName);

    List<Book> deleteBookById(Long id);
}
