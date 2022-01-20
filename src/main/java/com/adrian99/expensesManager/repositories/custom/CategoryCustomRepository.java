package com.adrian99.expensesManager.repositories.custom;

import com.adrian99.expensesManager.model.Category;

public interface CategoryCustomRepository {
    Category getByTitle(String title);
}
