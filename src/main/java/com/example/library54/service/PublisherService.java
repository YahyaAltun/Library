package com.example.library54.service;

import com.example.library54.domain.Publisher;
import com.example.library54.dto.PublisherDTO;
import com.example.library54.dto.mapper.PublisherMapper;
import com.example.library54.exception.BadRequestException;
import com.example.library54.exception.ResourceNotFoundException;
import com.example.library54.exception.message.ErrorMessage;
import com.example.library54.repository.PublisherRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@AllArgsConstructor
public class PublisherService {

    private PublisherRepository repository;
    private PublisherMapper publisherMapper;

    public Publisher createPublisher(PublisherDTO publisherDTO) {

        Publisher publisher1= publisherMapper.publisherDTOToPublisher(publisherDTO);
        repository.save(publisher1);
        return  publisher1;
    }

    public Page<PublisherDTO> getPublisherPage(Pageable pageable) {
        Page<Publisher> publishers = repository.findAll(pageable);
        Page<PublisherDTO> dtoPage = publishers.map(new Function<Publisher, PublisherDTO>() {
            @Override
            public PublisherDTO apply(Publisher publisher) {
                return publisherMapper.publisherToPublisherDTO(publisher);
            }
        });

        return dtoPage;
    }


    public PublisherDTO findById(Long id) {
        Publisher publisher = repository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.PUBLISHER_NOT_FOUND_MESSAGE, id)));
        return publisherMapper.publisherToPublisherDTO(publisher);
    }

    public Publisher updatePublisher(Long id, Publisher publisher) {
        Publisher foundPublisher = repository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.PUBLISHER_NOT_FOUND_MESSAGE, id)));
        if(foundPublisher.getBuiltIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
        foundPublisher.setId(id);
        foundPublisher.setName(publisher.getName());
        repository.save(foundPublisher);
        return foundPublisher;
    }


    public Publisher deleteById(Long id) {
        Publisher publisher = repository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.PUBLISHER_NOT_FOUND_MESSAGE, id)));
        if(publisher.getBuiltIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
        if(!publisher.getBooks().isEmpty()) {
            throw  new ResourceNotFoundException(ErrorMessage.PUBLİSHER_HAS_BOOK_MESSAGE);
        }
        repository.deleteById(id);
        return publisher;
    }
}
