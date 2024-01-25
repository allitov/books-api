package com.allitov.booksapi.web.mapper;

import com.allitov.booksapi.model.data.Book;
import com.allitov.booksapi.model.data.Category;
import com.allitov.booksapi.web.dto.request.BookRequest;
import com.allitov.booksapi.web.dto.response.BookListResponse;
import com.allitov.booksapi.web.dto.response.BookResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    default Book requestToBook(BookRequest request) {
        if (request == null) {
            return null;
        }

        Book.BookBuilder book = Book.builder();

        book.name(request.getName());
        book.author(request.getAuthor());
        book.category(new Category(null, request.getCategoryName()));
        book.publicationDate(request.getPublicationDate());

        return book.build();
    }

    default Book requestToBook(Long bookId, BookRequest request) {
        Book book = requestToBook(request);
        book.setId(bookId);

        return book;
    }

    default BookResponse bookToResponse(Book book) {
        if (book == null) {
            return null;
        }

        BookResponse bookResponse = new BookResponse();

        bookResponse.setId(book.getId());
        bookResponse.setName(book.getName());
        bookResponse.setAuthor(book.getAuthor());
        bookResponse.setCategoryName(book.getCategory().getName());
        bookResponse.setPublicationDate(book.getPublicationDate());

        return bookResponse;
    }

    default List<BookResponse> bookListToResponseList(List<Book> books) {
        if (books == null) {
            return null;
        }

        return books.stream().map(this::bookToResponse).toList();
    }

    default BookListResponse bookListToBookListResponse(List<Book> books) {
        BookListResponse response = new BookListResponse();
        response.setBooks(bookListToResponseList(books));

        return response;
    }
}
