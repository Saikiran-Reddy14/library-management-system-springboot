package com.practice.library_management.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.practice.library_management.entity.Book;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    Optional<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByCategoryNameContainingIgnoreCase(String categoryName);

    List<Book> findByAuthorNameContainingIgnoreCase(String authorName);

    boolean existsByTitle(String title);

}
