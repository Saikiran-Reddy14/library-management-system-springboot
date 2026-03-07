package com.practice.library_management.service;

import org.springframework.stereotype.Service;

import com.practice.library_management.dto.BookReq;
import com.practice.library_management.dto.BookRes;
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

}
