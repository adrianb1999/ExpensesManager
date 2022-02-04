package com.adrian99.expensesManager.services;

import java.util.List;

public interface CrudService<T, ID>{

    T findById(ID id);

    T save(T object);

    <S extends T> Iterable<S> saveAll(Iterable<S> entities);

    void deleteById(ID id);
}