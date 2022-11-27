package com.example.library54.controller;

import com.example.library54.dto.response.BookResponse;
import com.example.library54.dto.response.ReportResponse;
import com.example.library54.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
public class ReportController {

    private ReportService reportService;

    @GetMapping("/report")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<ReportResponse> getSomeStatistics() {

        ReportResponse response = reportService.getReportAboutAllData();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/unreturned-books")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Page<BookResponse>> getReportsWithPage(@RequestParam("page") int page,
                                                                 @RequestParam("size") int size,
                                                                 @RequestParam("sort") String prop,
                                                                 @RequestParam("type") Sort.Direction type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(type, prop));
        Page<BookResponse> bookResponse = reportService.findReportsWithPage(pageable);
        return ResponseEntity.ok(bookResponse);

    }

    @GetMapping("/report/expired-books")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<BookResponse>> getReportsWithPageExpiredBooks(@RequestParam("page") int page,
                                                                             @RequestParam("size") int size,
                                                                             @RequestParam("sort") String prop,
                                                                             @RequestParam("type") Sort.Direction type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(type, prop));
        List<BookResponse> bookResponse = reportService.findReportsWithPageExpiredBooks(pageable);
        return ResponseEntity.ok(bookResponse);

    }



    @GetMapping("/report/most-borrowers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Page> getReportMostBorrowers(@RequestParam(required = false,value = "page", defaultValue = "0") int page,
                                                       @RequestParam(required = false,value = "size", defaultValue = "20") int size ){

        Pageable pageable = PageRequest.of(page, size);
        Page bookResponse = reportService.findReportMostBorrowers(pageable);

        return ResponseEntity.ok(bookResponse);

    }

    @GetMapping("/report/most-popular-books")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity <List<Object>> getReportMostPopularBooks(@RequestParam(required = false,value = "amount", defaultValue = "10") int amount,
                                                                   @RequestParam(required = false,value = "page", defaultValue = "0") int page,
                                                                   @RequestParam(required = false,value = "size", defaultValue = "20") int size ){

        Pageable pageable = PageRequest.of(page, size);
        List<Object> bookResponse = reportService.findReportMostPopularBooks(amount,pageable);

        return ResponseEntity.ok(bookResponse);

    }
}
