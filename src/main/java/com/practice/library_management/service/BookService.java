package com.practice.library_management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.practice.library_management.dto.BookReq;
import com.practice.library_management.dto.BookRes;
import com.practice.library_management.dto.BookUpdateReq;
import com.practice.library_management.dto.PaginationRes;
import com.practice.library_management.entity.Book;
import com.practice.library_management.exception.ResourceExists;
import com.practice.library_management.exception.ResourceNotFound;
import com.practice.library_management.repo.BookRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepo bookRepo;

    @Transactional
    public BookRes addBook(BookReq request) {
        if (bookRepo.existsByTitle(request.title())) {
            throw new ResourceExists("Book already exists with title: " + request.title());
        }

        if (bookRepo.existsByIsbn(request.isbn())) {
            throw new ResourceExists("Book already exists with isbn: " + request.isbn());
        }

        Book newBook = Book.builder()
                .title(request.title())
                .authorName(request.authorName())
                .isbn(request.isbn())
                .categoryName(request.categoryName())
                .totalCopies(request.totalCopies())
                .availableCopies(request.totalCopies())
                .build();

        bookRepo.save(newBook);

        return toBookRes(newBook);
    }

    @Transactional(readOnly = true)
    public PaginationRes<BookRes> getAllBooks(int page, int size, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> books = bookRepo.findAll(pageable);

        List<BookRes> bookRes = books.getContent().stream().map(this::toBookRes).toList();

        return PaginationRes.<BookRes>builder()
                .data(bookRes)
                .pageNumber(books.getNumber())
                .pageSize(books.getSize())
                .totalPages(books.getTotalPages())
                .totalElements(books.getTotalElements())
                .hasNext(books.hasNext())
                .build();
    }

    @Transactional(readOnly = true)
    public BookRes getBookById(Long bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResourceNotFound("Book not found with id: " + bookId));
        return toBookRes(book);
    }

    @Transactional
    public BookRes updateBook(Long bookId, BookUpdateReq request) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResourceNotFound("Book not found with id: " + bookId));

        if (request.title() != null) {
            if (!request.title().equals(book.getTitle()) && bookRepo.existsByTitle(request.title())) {
                throw new ResourceExists("Book already exists with title: " + request.title());
            }
            book.setTitle(request.title());
        }
        if (request.isbn() != null) {
            if (!request.isbn().equals(book.getIsbn()) && bookRepo.existsByIsbn(request.isbn())) {
                throw new ResourceExists("Book already exists with ISBN: " + request.isbn());
            }
            book.setIsbn(request.isbn());
        }
        if (request.totalCopies() != null) {
            Long borrowedCopies = book.getTotalCopies() - book.getAvailableCopies();
            if (request.totalCopies() < borrowedCopies) {
                throw new IllegalArgumentException(
                        "Cannot set total copies to " + request.totalCopies() + ". Currently " + borrowedCopies
                                + " copies are borrowed");
            }
            book.setAvailableCopies(request.totalCopies() - borrowedCopies);
            book.setTotalCopies(request.totalCopies());
        }
        if (request.authorName() != null) {
            book.setAuthorName(request.authorName());
        }
        if (request.categoryName() != null) {
            book.setCategoryName(request.categoryName());
        }

        bookRepo.save(book);

        return BookRes.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .categoryName(book.getCategoryName())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .build();
    }

    private BookRes toBookRes(Book book) {
        return BookRes.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .categoryName(book.getCategoryName())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .build();
    }

}
