package com.example.library54.dto.mapper;

import com.example.library54.domain.Publisher;
import com.example.library54.dto.PublisherDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

    PublisherDTO publisherToPublisherDTO(Publisher publisher);

    @Mapping(target="books",ignore=true)
    Publisher publisherDTOToPublisher(PublisherDTO publisherDTO);

    Publisher PublisherDTOToPublisher(PublisherDTO publisherDTO);

    List<PublisherDTO> map(List<Publisher> publisher);
}
