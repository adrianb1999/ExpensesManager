package com.adrian99.expensesManager.services;

import java.util.List;

public interface CrudService<T, ID>{

    T findById(ID id);

    T save(T object);

    void deleteById(ID id);
}