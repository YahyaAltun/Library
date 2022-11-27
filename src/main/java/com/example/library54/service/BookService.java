package com.example.library54.service;

import com.example.library54.domain.Author;
import com.example.library54.domain.Book;
import com.example.library54.domain.Category;
import com.example.library54.domain.Publisher;
import com.example.library54.dto.BookDTO;
import com.example.library54.dto.BookInfoDTO;
import com.example.library54.dto.BookUpdateDTO;
import com.example.library54.dto.mapper.BookMapper;
import com.example.library54.exception.BadRequestException;
import com.example.library54.exception.ResourceNotFoundException;
import com.example.library54.exception.message.ErrorMessage;
import com.example.library54.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired

    private AuthorRepository authorRepository;
    @Autowired

    private CategoryRepository categoryRepository;
    @Autowired

    private PublisherRepository publisherRepository;
    @Autowired

    private LoanRepository loanRepository;
    @Autowired

    private BookMapper bookMapper;

    @Value("${file.uploaddir}")
    private String uploadPath;


    public Book createBookWithImage(BookDTO bookDTO, MultipartFile file) {
        Author author = authorRepository.findById(bookDTO.getAuthorId()).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.AUTHOR_NOT_FOUND_MESSAGE, bookDTO.getAuthorId())));

        Category category = categoryRepository.findById(bookDTO.getCategoryId()).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.CATEGORY_NOT_FOUND_MESSAGE, bookDTO.getCategoryId())));

        Publisher publisher = publisherRepository.findById(bookDTO.getPublisherId()).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.PUBLISHER_NOT_FOUND_MESSAGE, bookDTO.getPublisherId())));

        Book book = bookMapper.bookDTOToBook(bookDTO);


        // Normalize file name
        String fileName = StringUtils.
                cleanPath(file.getOriginalFilename());
        String extension = fileName.substring(fileName.
                lastIndexOf(".") + 1);

        UUID uuid = UUID.randomUUID();

        String nameImage = uuid.toString();
        nameImage = nameImage + "." + extension;


        book.setName(bookDTO.getName());
        book.setIsbn(bookDTO.getIsbn());
        book.setPageCount(bookDTO.getPageCount());
        book.setPublishDate(bookDTO.getPublishDate());
        book.setCategoryId(category);
        book.setPublisherId(publisher);
        book.setAuthorId(author);
        book.setShelfCode(bookDTO.getShelfCode());
        book.setFeatured(bookDTO.isFeatured());
        book.setImage(nameImage);

        bookRepository.save(book);

        try {
            // Check if the file's name contains invalid characters
            if (nameImage.contains("..")) {
                throw new BadRequestException("Sorry! Filename contains invalid path sequence " + nameImage);
            }
            Path path = Paths.get(uploadPath)
                    .toAbsolutePath().normalize();

            try {
                Files.createDirectories(path);
            } catch (Exception ex) {
                throw new BadRequestException("Could not create the directory where the uploaded files will be stored.");
            }
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = path.resolve(nameImage);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // return nameImage;
        } catch (IOException ex) {
            throw new BadRequestException("Could not store file " + nameImage + ". Please try again!");
        }
        return book;
    }

    @Transactional(readOnly = true)
    public Page<BookInfoDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findBookAll(pageable);
    }


    public Resource loadFileAsResource(String fileName) {
        try {
            Path path = Paths.get(uploadPath)
                    .toAbsolutePath().normalize();

            Resource resource = new UrlResource(path.toUri().
                    resolve(fileName).normalize());


            if (resource.exists()) {
                return resource;
            } else {
                throw new BadRequestException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new BadRequestException("File not found " + fileName);
        }
    }

    public Book deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.BOOK_NOT_FOUND_MESSAGE, id)));

        boolean exists = loanRepository.existsByBookId(book);

        if (exists) {
            throw new BadRequestException(ErrorMessage.BOOK_LOANED_OUT);
        }
        bookRepository.deleteById(id);

        return book;
    }


    public Book findBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.BOOK_NOT_FOUND_MESSAGE, id)));

        return book;
    }


    public Book updateBook(Long id, BookUpdateDTO bookUpdateDTO) {
        Book foundBook = bookRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessage.BOOK_NOT_FOUND_MESSAGE, id)));

        if (foundBook.isBuiltIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }

        Author author = authorRepository.findById(bookUpdateDTO.getAuthorId()).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.AUTHOR_NOT_FOUND_MESSAGE, bookUpdateDTO.getAuthorId())));

        Category category = categoryRepository.findById(bookUpdateDTO.getCategoryId()).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.CATEGORY_NOT_FOUND_MESSAGE, bookUpdateDTO.getCategoryId())));

        Publisher publisher = publisherRepository.findById(bookUpdateDTO.getPublisherId()).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.PUBLISHER_NOT_FOUND_MESSAGE, bookUpdateDTO.getPublisherId())));

        Book book = bookMapper.bookUpdateDTOToBook(bookUpdateDTO);
        book.setImage(foundBook.getImage());
        book.setId(foundBook.getId());
        book.setAuthorId(author);
        book.setCategoryId(category);
        book.setPublisherId(publisher);

        bookRepository.save(book);
        return book;
    }

    @Autowired
    EntityManager entityManager;

    public Page findAllWithPageForMemberQuery(String name, Long cat, Long author, Long publisher,
                                              Pageable pageable) throws BadRequestException {
        if (name == null && cat == null && author == null && publisher == null) {

            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("select b.id, b.name, b.isbn from Book b where");
            if (name != null) {
                sb.append(" b.name= :name and");
            }
            if (cat != null) {
                sb.append(" b.categoryId.id= :cat and");
            }
            if (author != null) {
                sb.append(" b.authorId.id= :author and");
            }
            if (publisher != null) {
                sb.append(" b.publisherId.id= :publisher and");
            }
            sb.append(" b.active=true");

            Query query = entityManager.createQuery(sb.toString());
            if (name != null) {
                query.setParameter("name", name);
            }
            if (cat != null) {
                query.setParameter("cat", cat);
            }
            if (author != null) {
                query.setParameter("author", author);
            }
            if (publisher != null) {
                query.setParameter("publisher", publisher);
            }
            List books = new ArrayList<>();
            books = query.getResultList();

            final int start = (int) pageable.getOffset();
            final int end = Math.min((start + pageable.getPageSize()), books.size());
            final Page<Book> page = new PageImpl<>(books.subList(start, end), pageable, books.size());

            return page;

        }


    }

    public Page findAllWithPageForAdminQuery(String name, Long cat, Long author,
                                             Long publisher, Pageable pageable) throws BadRequestException {

        if (name == null && cat == null && author == null && publisher == null) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("select b.id, b.name, b.isbn from Book b where");
            if (name != null) {
                sb.append(" b.name= :name and");
            }
            if (cat != null) {
                sb.append(" b.categoryId.id= :cat and");
            }
            if (author != null) {
                sb.append(" b.authorId.id= :author and");
            }
            if (publisher != null) {
                sb.append(" b.publisherId.id= :publisher and");
            }
            sb.append(" b.id is not null");
            Query query = entityManager.createQuery(sb.toString());
            if (name != null) {
                query.setParameter("name", name);
            }
            if (cat != null) {
                query.setParameter("cat", cat);
            }
            if (author != null) {
                query.setParameter("author", author);
            }
            if (publisher != null) {
                query.setParameter("publisher", publisher);
            }
            List books = new ArrayList<>();
            books = query.getResultList();

            final int start = (int) pageable.getOffset();
            final int end = Math.min((start + pageable.getPageSize()), books.size());
            final Page<Book> page = new PageImpl<>(books.subList(start, end), pageable, books.size());

            return page;
        }
    }
}
