package com.example.library54.dto.mapper;

import com.example.library54.domain.Author;
import com.example.library54.dto.AuthorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorDTO authorToAuthorDTO(Author author);

    @Mapping(target="books",ignore=true)
    Author authorDTOToAuthor(AuthorDTO authorDTO);

    List<AuthorDTO> map(List<Author> author);

}
