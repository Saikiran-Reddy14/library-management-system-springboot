package com.practice.library_management.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.practice.library_management.dto.BorrowReq;
import com.practice.library_management.dto.BorrowRes;
import com.practice.library_management.entity.Book;
import com.practice.library_management.entity.BorrowRecord;
import com.practice.library_management.entity.BorrowStatus;
import com.practice.library_management.entity.User;
import com.practice.library_management.exception.CustomException;
import com.practice.library_management.exception.ResourceNotFound;
import com.practice.library_management.repo.BookRepo;
import com.practice.library_management.repo.BorrowRecordRepo;
import com.practice.library_management.repo.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BorrowService {

        private final BookRepo bookRepo;
        private final UserRepo userRepo;
        private final BorrowRecordRepo borrowRepo;

        private static final int BORROW_DAYS = 14;

        @Transactional
        public BorrowRes borrowBook(BorrowReq request, String email) {
                User user = userRepo.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFound("User does not exist with email: " + email));
                Book book = bookRepo.findByIdForUpdate(request.bookId())
                                .orElseThrow(() -> new ResourceNotFound("Book not found"));

                if (borrowRepo.existsByBookAndUserAndBorrowStatusIn(
                                book,
                                user,
                                List.of(BorrowStatus.BORROWED, BorrowStatus.OVERDUE))) {
                        throw new CustomException("You already have an active borrowing record for this book.");
                }

                if (book.getAvailableCopies() <= 0) {
                        throw new CustomException("Book is not available for borrowing");
                }
                book.setAvailableCopies(book.getAvailableCopies() - 1);

                BorrowRecord record = BorrowRecord.builder()
                                .book(book)
                                .user(user)
                                .borrowStatus(BorrowStatus.BORROWED)
                                .borrowDate(LocalDate.now())
                                .returnDate(null)
                                .dueDate(LocalDate.now().plusDays(BORROW_DAYS))
                                .build();

                record = borrowRepo.save(record);

                return BorrowRes.builder()
                                .recordId(record.getRecordId())
                                .bookTitle(record.getBook().getTitle())
                                .username(record.getUser().getUsername())
                                .borrowDate(record.getBorrowDate())
                                .dueDate(record.getDueDate())
                                .returnDate(record.getReturnDate())
                                .borrowStatus(record.getBorrowStatus())
                                .build();
        }

        @Transactional
        public BorrowRes returnBook(Long recordId, String email) {
                BorrowRecord record = borrowRepo.findById(recordId)
                                .orElseThrow(() -> new ResourceNotFound("Borrow record not found"));

                if (!record.getUser().getEmail().equals(email)) {
                        throw new CustomException("You can only return books you have borrowed.");
                }

                if (record.getBorrowStatus() != BorrowStatus.BORROWED &&
                                record.getBorrowStatus() != BorrowStatus.OVERDUE) {
                        throw new CustomException("This book is not currently borrowed.");
                }

                Book book = record.getBook();
                book.setAvailableCopies(book.getAvailableCopies() + 1);

                record.setReturnDate(LocalDate.now());
                record.setBorrowStatus(BorrowStatus.RETURNED);
                borrowRepo.save(record);

                return BorrowRes.builder()
                                .recordId(record.getRecordId())
                                .bookTitle(record.getBook().getTitle())
                                .username(record.getUser().getUsername())
                                .borrowDate(record.getBorrowDate())
                                .dueDate(record.getDueDate())
                                .returnDate(record.getReturnDate())
                                .borrowStatus(record.getBorrowStatus())
                                .build();
        }

        @Transactional
        @Scheduled(cron = "0 0 0 * * ?")
        public void updateOverdueBooks() {

                List<BorrowRecord> overdueRecords = borrowRepo.findByBorrowStatusAndDueDateBefore(
                                BorrowStatus.BORROWED,
                                LocalDate.now());

                for (BorrowRecord record : overdueRecords) {
                        record.setBorrowStatus(BorrowStatus.OVERDUE);
                }
        }
}
