package com.practice.library_management.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.library_management.entity.Book;
import com.practice.library_management.entity.BorrowRecord;
import com.practice.library_management.entity.BorrowStatus;
import com.practice.library_management.entity.User;

public interface BorrowRecordRepo extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByUser(User user);

    List<BorrowRecord> findByBorrowStatus(BorrowStatus status);

    List<BorrowRecord> findByUserAndBorrowStatus(User user, BorrowStatus status);

    boolean existsByBookAndUserAndBorrowStatusIn(Book book, User user, List<BorrowStatus> statuses);

}
