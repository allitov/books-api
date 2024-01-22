package com.allitov.booksapi.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessage {

    public final String BLANK_BOOK_NAME = "Blank name must be specified";

    public final String BLANK_AUTHOR_NAME = "Author name must be specified";

    public final String BLANK_CATEGORY_NAME = "Category name must be specified";

    public final String BLANK_PUBLICATION_DATE = "Publication date must be specified";

    public final String INVALID_BOOK_NAME_LENGTH = "Book name length must be <= {max} symbols";

    public final String INVALID_AUTHOR_NAME_LENGTH = "Author name length must be <= {max} symbols";

    public final String INVALID_CATEGORY_NAME_LENGTH = "Category name length must be <= {max} symbols";

    public final String BOOK_BY_ID_NOT_FOUND = "Book with id '%d' not found";

    public final String BOOK_BY_NAME_AND_AUTHOR_NOT_FOUND = "Book with name '%s' and author '%s' not found";
}
