package com.allitov.booksapi.model.repository;

import com.allitov.booksapi.model.data.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findCategoryByName(String categoryName);
}
