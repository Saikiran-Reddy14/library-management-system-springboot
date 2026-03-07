package com.practice.library_management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.practice.library_management.dto.BookReq;
import com.practice.library_management.dto.BookRes;
import com.practice.library_management.dto.PaginationRes;
import com.practice.library_management.entity.Book;
import com.practice.library_management.exception.ResourceExists;
import com.practice.library_management.exception.ResourceNotFound;
import com.practice.library_management.repo.BookRepo;
import com.practice.library_management.repo.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepo bookRepo;
    private final UserRepo userRepo;

    public BookRes addBook(BookReq request, String email) {
        userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFound("User does not exists"));
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

        return BookRes.builder()
                .bookId(newBook.getBookId())
                .title(newBook.getTitle())
                .authorName(newBook.getAuthorName())
                .isbn(newBook.getIsbn())
                .categoryName(newBook.getCategoryName())
                .totalCopies(newBook.getTotalCopies())
                .availableCopies(newBook.getAvailableCopies())
                .build();
    }

    public PaginationRes<BookRes> getAllBooks(int page, int size, String sortBy, String sortOrder, String email) {
        userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFound("User does not exists"));
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> books = bookRepo.findAll(pageable);

        List<BookRes> bookRes = books.getContent().stream().map(book -> BookRes.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .categoryName(book.getCategoryName())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .build()).toList();

        return PaginationRes.<BookRes>builder()
                .data(bookRes)
                .pageNumber(books.getNumber())
                .pageSize(books.getSize())
                .totalPages(books.getTotalPages())
                .totalElements(books.getTotalElements())
                .hasNext(books.hasNext())
                .build();
    }

}
