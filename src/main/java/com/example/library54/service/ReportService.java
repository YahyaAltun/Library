package com.example.library54.service;

import com.example.library54.domain.Book;
import com.example.library54.dto.mapper.BookMapper;
import com.example.library54.dto.response.BookResponse;
import com.example.library54.dto.response.ReportResponse;
import com.example.library54.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReportService {

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private PublisherRepository publisherRepository;
    private CategoryRepository categoryRepository;
    private LoanRepository loanRepository;
    private  UserRepository userRepository;
    private  RoleRepository roleRepository;
    private BookMapper bookMapper;

    public ReportResponse getReportAboutAllData() {

        ReportResponse report = new ReportResponse();

        LocalDateTime now = LocalDateTime.now();

        report.setBooks(bookRepository.count());
        report.setAuthors(authorRepository.count());
        report.setPublishers(publisherRepository.count());
        report.setCategories(categoryRepository.count());
        report.setLoans(loanRepository.count());
        report.setUnReturnedBooks(loanRepository.findLoanByReturnDateIsNull().stream().count());
        report.setExpiredBooks(loanRepository.findLoanByReturnDateIsNull().
                stream().filter(t -> t.getExpireDate().isBefore(now)).count());
        report.setMembers(roleRepository.countOfMember());

        return report;
    }

    public Page<BookResponse> findReportsWithPage(Pageable pageable) {

        Page<Book> books = bookRepository.findBookByLoanableIsFalse(pageable);

        Page<BookResponse>dtoPage =  books.map(book -> bookMapper.bookToBookResponse(book));
        return dtoPage;
    }

    public List<BookResponse> findReportsWithPageExpiredBooks(Pageable pageable) {

        LocalDateTime now = LocalDateTime.now();

        List<Book> books= (loanRepository.findLoanByReturnDateIsNull().
                stream().filter(t -> t.getExpireDate().isBefore(now)).map(t->t.getBookId()).
                collect(Collectors.toList()));

        List<BookResponse>dtoPage =  bookMapper.map(books);
        return dtoPage;
    }



    public List<Object> findReportMostPopularBooks(int amount, Pageable pageable) {

        Page<Object> mostPopularBooks = loanRepository.findMostPopularBooks(pageable);
        return         mostPopularBooks.stream().limit(amount).collect(Collectors.toList());

    }

    public Page findReportMostBorrowers(Pageable pageable) {

        Page mostBorrowers = loanRepository.findMostBorrowers(pageable);

        return mostBorrowers;
    }

}