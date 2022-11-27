package com.example.library54.controller;

import com.example.library54.domain.Category;
import com.example.library54.dto.CategoryDTO;
import com.example.library54.dto.response.CategoryResponse;
import com.example.library54.service.CategoryService;
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
public class CategoryController {

    private CategoryService categoryService;

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){

        Category newCategory  = categoryService.createCategory(categoryDTO);
        CategoryResponse response = new CategoryResponse();
        response.setId(newCategory.getId());
        response.setName(newCategory.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/categories")
    public ResponseEntity<Page<CategoryDTO>> getAllCategoriesByPage(@RequestParam("page") int page,
                                                              @RequestParam("size") int size,
                                                              @RequestParam("sort") String prop,
                                                              @RequestParam("direction") Sort.Direction direction){

        Pageable pageable= PageRequest.of(page, size, Sort.by(direction,prop));
        Page<CategoryDTO> userDTOPage=categoryService.getCategoryPage(pageable);
        return ResponseEntity.ok(userDTOPage);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse>  findById(@PathVariable("id") Long id){
        CategoryDTO categoryDTO= categoryService.findById(id);
        CategoryResponse response = new CategoryResponse();
        response.setId(categoryDTO.getId());
        response.setName(categoryDTO.getName());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable("id") Long id, @Valid @RequestBody Category category){
        Category category1 = categoryService.updateCategory(id,category);
        CategoryResponse response = new CategoryResponse();
        response.setId(category1.getId());
        response.setName(category1.getName());
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> deleteById(@PathVariable("id") Long id){
        Category category= categoryService.deleteById(id);
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
