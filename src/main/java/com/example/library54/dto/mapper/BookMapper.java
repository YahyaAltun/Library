package com.example.library54.dto.mapper;

import com.example.library54.domain.Book;
import com.example.library54.dto.BookDTO;
import com.example.library54.dto.BookUpdateDTO;
import com.example.library54.dto.response.BookResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "publisherId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "image", ignore = true)
    Book bookDTOToBook(BookDTO bookDTO);

    List<BookResponse> map(List<Book> book);

    BookResponse bookToBookResponse(Book book);

    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "publisherId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "image", ignore = true)
    Book bookUpdateDTOToBook(BookUpdateDTO bookUpdateDTO);

}
