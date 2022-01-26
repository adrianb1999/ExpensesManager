package com.adrian99.expensesManager.services;

import com.adrian99.expensesManager.model.Category;
import com.adrian99.expensesManager.repositories.custom.CategoryCustomRepository;

public interface CategoryService extends CrudService<Category, Long>,
                                         CategoryCustomRepository{
}
