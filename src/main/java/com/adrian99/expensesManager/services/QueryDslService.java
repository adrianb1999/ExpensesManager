package com.adrian99.expensesManager.services;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public interface QueryDslService <T> {
     Optional<T> findOne(Predicate predicate);
     Iterable<T> findAll(Predicate predicate);
     Iterable<T> findAll(Predicate predicate, Sort sort);
}