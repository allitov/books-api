package com.allitov.booksapi.web.dto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookListResponse {

    private List<BookResponse> books = new ArrayList<>();
}
