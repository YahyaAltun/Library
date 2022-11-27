package com.example.library54.dto.mapper;

import com.example.library54.domain.Category;
import com.example.library54.dto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO categoryToCategoryDTO(Category category);

    @Mapping(target="books",ignore=true)
    Category categoryDTOToCategory(CategoryDTO categoryDTO);

    List<CategoryDTO> map(List<Category> category);

}
