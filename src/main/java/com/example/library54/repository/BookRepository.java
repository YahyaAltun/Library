package com.example.library54.repository;

import com.example.library54.domain.Book;
import com.example.library54.dto.BookInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findBookByLoanableIsFalse(Pageable pageable);

    @Query("select new com.example.library54.dto.BookInfoDTO(book) from Book book")
    Page<BookInfoDTO> findBookAll(Pageable pageable);
}
