package com.example.library54.dto;

import com.example.library54.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookInfoDTO {

    private String name;
    private String isbn;
    private int pageCount;
    private String authorId;
    private String publisherId;
    private int publishDate;
    private String categoryId;
    private File image;
    private String shelfCode;
    private boolean featured;

    public BookInfoDTO(Book book) {
        this.name = book.getName();
        this.isbn = book.getIsbn();
        this.pageCount = book.getPageCount();
        this.publishDate = book.getPublishDate();
        this.shelfCode = book.getShelfCode();
        this.featured = book.isFeatured();
        this.authorId = book.getAuthorId().getName();
        this.publisherId = book.getPublisherId().getName();
        this.categoryId = book.getCategoryId().getName();
    }

}
