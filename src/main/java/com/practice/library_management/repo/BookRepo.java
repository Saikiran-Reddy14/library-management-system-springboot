package com.practice.library_management.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.practice.library_management.entity.Book;

import jakarta.persistence.LockModeType;

import java.util.Optional;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    Optional<Book> findByTitleContainingIgnoreCase(String title);

    Page<Book> findByCategoryNameContainingIgnoreCase(String categoryName, Pageable pageable);

    Page<Book> findByAuthorNameContainingIgnoreCase(String authorName, Pageable pageable);

    Page<Book> findByAvailableCopiesGreaterThan(Long copies, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.authorName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.categoryName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Book> searchBooks(String query, Pageable pageable);

    boolean existsByTitle(String title);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Book b WHERE b.bookId = :id")
    Optional<Book> findByIdForUpdate(Long id);

}
