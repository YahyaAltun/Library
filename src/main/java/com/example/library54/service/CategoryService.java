package com.example.library54.service;

import com.example.library54.domain.Category;
import com.example.library54.dto.CategoryDTO;
import com.example.library54.dto.mapper.CategoryMapper;
import com.example.library54.exception.BadRequestException;
import com.example.library54.exception.ResourceNotFoundException;
import com.example.library54.exception.message.ErrorMessage;
import com.example.library54.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CategoryService {

    private CategoryRepository repository;
    private CategoryMapper categoryMapper;

    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);
        repository.save(category);
        return category;
    }

    public List<CategoryDTO> getAll() {
        List<Category> categoryList = repository.findAll();
        return categoryMapper.map(categoryList);
    }

    public CategoryDTO findById(Long id) {
        Category category = repository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.CATEGORY_NOT_FOUND_MESSAGE, id)));
        return categoryMapper.categoryToCategoryDTO(category);
    }

    public Category updateCategory(Long id, Category category) {
        Category foundCategory = repository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.CATEGORY_NOT_FOUND_MESSAGE, id)));
        if (foundCategory.getBuiltIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
        foundCategory.setId(id);
        foundCategory.setName(category.getName());
        repository.save(foundCategory);
        return foundCategory;
    }

    public Category deleteById(Long id) {
        Category category = repository.findById(id).orElseThrow(() -> new
                ResourceNotFoundException(String.format(ErrorMessage.CATEGORY_NOT_FOUND_MESSAGE, id)));
        if (category.getBuiltIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
        if (!category.getBooks().isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessage.CATEGORY_HAS_BOOK_MESSAGE);
        }
        repository.deleteById(id);
        return category;
    }

    public Page<CategoryDTO> getCategoryPage(Pageable pageable) {
        Page<Category> categories = repository.findAll(pageable);
        Page<CategoryDTO> dtoPage = categories.map(category -> categoryMapper.categoryToCategoryDTO(category));

        return dtoPage;
    }
}