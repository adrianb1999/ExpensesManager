package com.adrian99.expensesManager.services.implementation;

import com.adrian99.expensesManager.model.Category;
import com.adrian99.expensesManager.repositories.custom.implementation.CategoryCustomRepositoryImpl;
import com.adrian99.expensesManager.repositories.CategoryRepository;
import com.adrian99.expensesManager.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryCustomRepositoryImpl categoryCustomRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryCustomRepositoryImpl categoryCustomRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryCustomRepository = categoryCustomRepository;
    }

    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        categoryRepository.findAll().forEach(categories::add);
        return categories;
    }

    @Override
    public Category findById(Long aLong) {
        return categoryRepository.findById(aLong).orElse(null);
    }

    @Override
    public Category save(Category object) {
        return categoryRepository.save(object);
    }

    @Override
    public void delete(Category object) {
        categoryRepository.delete(object);
    }

    @Override
    public void deleteById(Long aLong) {
        categoryRepository.deleteById(aLong);
    }

    @Override
    public Category getByTitle(String title) {
        return categoryCustomRepository.getByTitle(title);
    }
}
