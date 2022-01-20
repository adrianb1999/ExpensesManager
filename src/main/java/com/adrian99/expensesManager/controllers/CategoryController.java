package com.adrian99.expensesManager.controllers;

import com.adrian99.expensesManager.model.Category;
import com.adrian99.expensesManager.services.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public List<Category> findAll() {
        return categoryService.findAll();
    }

    @PostMapping("/categories")
    public Category saveCategory(@RequestBody Category category) {
        return categoryService.save(category);
    }

    @GetMapping("categories/{id}")
    public Category findById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @PutMapping("/categories/{id}")
    public Category update(@RequestBody Category newCategory, @PathVariable Long id) {
        Category updateCategory = categoryService.findById(id);

        if (updateCategory == null) {
            newCategory.setId(id);
            return categoryService.save(newCategory);
        }

        updateCategory.setName(newCategory.getName());

        return categoryService.save(updateCategory);
    }

    @DeleteMapping("categories/{id}") //in mod normal trebuie responseEntity!
    public void delete(@PathVariable Long id) {
        categoryService.deleteById(id);
    }
}
