package com.adrian99.expensesManager.repositories.custom.implementation;

import com.adrian99.expensesManager.model.Category;
import com.adrian99.expensesManager.repositories.custom.CategoryCustomRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryCustomRepositoryImpl implements CategoryCustomRepository {
    @Override
    public Category getByTitle(String title) {
        return null; //TODO Needs work
    }
}
