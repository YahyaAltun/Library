package com.example.library54.controller;

import com.example.library54.domain.Book;
import com.example.library54.dto.BookDTO;
import com.example.library54.dto.BookInfoDTO;
import com.example.library54.dto.BookUpdateDTO;
import com.example.library54.dto.response.BookResponse;
import com.example.library54.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@AllArgsConstructor
public class BookController {

    private BookService bookService;

    @DeleteMapping("/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> deleteBook(@PathVariable("id") Long id) {
        Book book = bookService.deleteBook(id);

        BookResponse response = new BookResponse();
        response.setId(id);
        response.setName(book.getName());
        response.setIsbn(book.getIsbn());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable("id") Long id) {
        Book book = bookService.findBookById(id);

        BookResponse response = new BookResponse();
        response.setId(id);
        response.setName(book.getName());
        response.setIsbn(book.getIsbn());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> updateBook(@PathVariable("id") Long id, @Valid
    @RequestBody BookUpdateDTO bookUpdateDTO) {
        Book book = bookService.updateBook(id, bookUpdateDTO);

        BookResponse response = new BookResponse();
        response.setId(id);
        response.setName(book.getName());
        response.setIsbn(book.getIsbn());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/books")
    @PreAuthorize("hasRole('ANONYMOUS') or hasRole('MEMBER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Page> getAllWithPageForMemberQuery(@RequestParam(required = false, value = "q") String name,
                                                             @RequestParam(required = false, value = "cat") Long categoryId,
                                                             @RequestParam(required = false, value = "author") Long authorId,
                                                             @RequestParam(required = false, value = "publisher") Long publisherId,
                                                             @RequestParam(required = false, value = "page", defaultValue = "0") int page,
                                                             @RequestParam(required = false, value = "size", defaultValue = "20") int size,
                                                             @RequestParam(required = false, value = "sort", defaultValue = "name") String prop,
                                                             @RequestParam(required = false, value = "direction", defaultValue = "ASC") Sort.Direction direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
        Page bookPage = bookService.findAllWithPageForMemberQuery(name, categoryId, authorId, publisherId, pageable);

        return ResponseEntity.ok(bookPage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/books/admin")
    public ResponseEntity<Page> getAllWithPageForAdminQuery(@RequestParam(required = false, value = "q") String name,
                                                            @RequestParam(required = false, value = "cat") Long categoryId,
                                                            @RequestParam(required = false, value = "author") Long authorId,
                                                            @RequestParam(required = false, value = "publisher") Long publisherId,
                                                            @RequestParam(required = false, value = "page", defaultValue = "0") int page,
                                                            @RequestParam(required = false, value = "size", defaultValue = "20") int size,
                                                            @RequestParam(required = false, value = "sort", defaultValue = "name") String prop,
                                                            @RequestParam(required = false, value = "direction", defaultValue = "ASC") Sort.Direction direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
        Page bookPage = bookService.findAllWithPageForAdminQuery(name, categoryId, authorId, publisherId, pageable);

        return ResponseEntity.ok(bookPage);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("books")
    public ResponseEntity<BookResponse> createBookWithImage(@RequestParam(required = false, value = "image") MultipartFile image,
                                                            @RequestParam(required = false, value = "book") String book) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        BookDTO bookDTO = mapper.readValue(book, BookDTO.class);
        System.out.println("book = " + bookDTO);

        String folder = System.getProperty("user.dir");

        Book book1 =bookService.createBookWithImage(bookDTO,image);

        System.out.println("folder = " + folder);

        BookResponse response = new BookResponse();

        response.setId(book1.getId());
        response.setName(book1.getName());
        response.setIsbn(book1.getIsbn());
        return ResponseEntity.ok(response);
    }

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER') or hasRole('EMPLOYEE')")
    @GetMapping("/book/image/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = bookService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("books/all")
    public ResponseEntity<Page<BookInfoDTO>> getAllBooks(@RequestParam("page") int page,
                                                         @RequestParam("size") int size,
                                                         @RequestParam("sort") String prop,
                                                         @RequestParam("direction") Sort.Direction direction){

        Pageable pageable= PageRequest.of(page, size, Sort.by(direction,prop));
        Page<BookInfoDTO> bookInfoDTOPage= bookService.getAllBooks(pageable);

        return ResponseEntity.ok(bookInfoDTOPage);
    }
}
