package com.example.library54.controller;

import com.example.library54.domain.Author;
import com.example.library54.dto.AuthorDTO;
import com.example.library54.dto.response.AuthorResponse;
import com.example.library54.service.AuthorService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping
@AllArgsConstructor
public class AuthorController {

    private AuthorService authorService;

    @PostMapping("/authors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AuthorDTO authorDTO){

        Author newAuthor  = authorService.createAuthor(authorDTO);
        AuthorResponse response = new AuthorResponse();
        response.setId(newAuthor.getId());
        response.setName(newAuthor.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/authors")
    public ResponseEntity<Page<AuthorDTO>> getAllUserByPage(@RequestParam("page") int page,
                                                            @RequestParam("size") int size,
                                                            @RequestParam("sort") String prop,
                                                            @RequestParam("direction") Sort.Direction direction){

        Pageable pageable= PageRequest.of(page, size, Sort.by(direction,prop));
        Page<AuthorDTO> userDTOPage=authorService.getAuthorPage(pageable);
        return ResponseEntity.ok(userDTOPage);
    }

    @GetMapping("/authors/{id}")
    public ResponseEntity<AuthorResponse>  findById(@PathVariable("id") Long id){
        AuthorDTO authorDTO= authorService.findById(id);
        AuthorResponse response = new AuthorResponse();
        response.setId(authorDTO.getId());
        response.setName(authorDTO.getName());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/authors/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponse> updateAuthor(@PathVariable("id") Long id, @Valid @RequestBody Author author){
        Author author1 = authorService.updateAuthor(id,author);
        AuthorResponse response = new AuthorResponse();
        response.setId(author1.getId());
        response.setName(author1.getName());
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    @DeleteMapping("/authors/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponse> deleteById(@PathVariable("id") Long id){
        Author author= authorService.deleteById(id);
        AuthorResponse response = new AuthorResponse();
        response.setId(author.getId());
        response.setName(author.getName());
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
}
