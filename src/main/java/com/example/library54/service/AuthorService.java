package com.example.library54.service;

import com.example.library54.domain.Author;
import com.example.library54.dto.AuthorDTO;
import com.example.library54.dto.mapper.AuthorMapper;
import com.example.library54.exception.BadRequestException;
import com.example.library54.exception.ResourceNotFoundException;
import com.example.library54.exception.message.ErrorMessage;
import com.example.library54.repository.AuthorRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@AllArgsConstructor
@Service
public class AuthorService {

    private AuthorRepository repository;
    private AuthorMapper authorMapper;

    public Author createAuthor(AuthorDTO authorDTO) {
        Author author = authorMapper.authorDTOToAuthor(authorDTO);
        repository.save(author);
        return author;
    }

    public AuthorDTO findById(Long id) {
        Author author = repository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.AUTHOR_NOT_FOUND_MESSAGE,id)));
        return authorMapper.authorToAuthorDTO(author);
    }

    public Author updateAuthor(Long id, Author author) {
        Author foundAuthor = repository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.AUTHOR_NOT_FOUND_MESSAGE,id)));
        if (foundAuthor.getBuiltIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
        foundAuthor.setId(id);
        foundAuthor.setName(author.getName());
        repository.save(foundAuthor);
        return foundAuthor;
    }

    public Author deleteById(Long id) {
        Author author = repository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.AUTHOR_NOT_FOUND_MESSAGE,id)));
        if (author.getBuiltIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
        if (!author.getBooks().isEmpty()) {
            throw new ResourceNotFoundException (ErrorMessage.AUTHOR_HAS_BOOK_MESSAGE);
        }
        repository.deleteById(id);
        return author;
    }

    public Page<AuthorDTO> getAuthorPage(Pageable pageable) {
        Page<Author> authors = repository.findAll(pageable);
        Page<AuthorDTO> dtoPage = authors.map(new Function<Author, AuthorDTO>() {
            @Override
            public AuthorDTO apply(Author author) {
                return authorMapper.authorToAuthorDTO(author);
            }
        });

        return dtoPage;
    }
}
