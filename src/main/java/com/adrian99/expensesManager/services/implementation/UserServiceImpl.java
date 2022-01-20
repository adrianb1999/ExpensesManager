package com.adrian99.expensesManager.services.implementation;

import com.adrian99.expensesManager.customQueries.UserQueries;
import com.adrian99.expensesManager.model.User;
import com.adrian99.expensesManager.repositories.UserRepository;
import com.adrian99.expensesManager.services.UserService;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserQueries userQueries;

    public UserServiceImpl(UserRepository userRepository, UserQueries userQueries) {
        this.userRepository = userRepository;
        this.userQueries = userQueries;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @Override
    public User findById(Long aLong) {
        return userRepository.findById(aLong).orElse(null);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(User object) {
        userRepository.delete(object);
    }

    @Override
    public void deleteById(Long aLong) {
        userRepository.deleteById(aLong);
    }

    @Override
    public Optional<User> findOne(Predicate predicate) {
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll(Predicate predicate) {
        return userRepository.findAll(predicate);
    }

    @Override
    public Iterable<User> findAll(Predicate predicate, Sort sort) {
        return null;
    }

    @Override
    public Iterable<User> findAll(Predicate predicate, OrderSpecifier<?>... orders) {
        return null;
    }

    @Override
    public Iterable<User> findAll(OrderSpecifier<?>... orders) {
        return null;
    }

    @Override
    public Page<User> findAll(Predicate predicate, Pageable pageable) {
        return null;
    }

    @Override
    public long count(Predicate predicate) {
        return 0;
    }

    @Override
    public boolean exists(Predicate predicate) {
        return false;
    }

    @Override
    public <S extends User, R> R findBy(Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
