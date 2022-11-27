package com.example.library54.controller;

import com.example.library54.domain.Publisher;
import com.example.library54.dto.PublisherDTO;
import com.example.library54.dto.response.PublisherResponse;
import com.example.library54.service.PublisherService;
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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@AllArgsConstructor
public class PublisherController {

    private PublisherService publisherService;

    @PostMapping("/publishers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherResponse> createPublisher(@Valid @RequestBody PublisherDTO publisherDTO) {

        Publisher newPublisher = publisherService.createPublisher(publisherDTO);

        PublisherResponse response = new PublisherResponse();
        response.setId(newPublisher.getId());
        response.setName(newPublisher.getName());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ANONYMOUS')")
    @GetMapping("/publishers")  public ResponseEntity<Page<PublisherDTO>> getAllPublisherByPage(@RequestParam("page") int page,
                                                                                                @RequestParam("size") int size,
                                                                                                @RequestParam("sort") String prop,
                                                                                                @RequestParam("direction") Sort.Direction direction){

        Pageable pageable= PageRequest.of(page, size, Sort.by(direction,prop));
        Page<PublisherDTO> userDTOPage=publisherService.getPublisherPage(pageable);
        return ResponseEntity.ok(userDTOPage);
    }


    @PreAuthorize("hasRole('ANONYMOUS')")
    @GetMapping("/publishers/{id}")
    public ResponseEntity<PublisherResponse>  findById(@PathVariable("id") Long id){
        PublisherDTO publisherDTO= publisherService.findById(id);
        PublisherResponse response = new PublisherResponse();
        response.setId(publisherDTO.getId());
        response.setName(publisherDTO.getName());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @DeleteMapping("/publishers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherResponse> deleteById(@PathVariable("id") Long id){
        Publisher publisher= publisherService.deleteById(id);
        PublisherResponse response = new PublisherResponse();
        response.setId(publisher.getId());
        response.setName(publisher.getName());
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PutMapping("/publishers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherResponse> updatePublisher(@PathVariable("id") Long id, @Valid @RequestBody Publisher publisher){
        Publisher publisher1 = publisherService.updatePublisher(id,publisher);
        PublisherResponse response = new PublisherResponse();
        response.setId(publisher1.getId());
        response.setName(publisher1.getName());
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
}
