package com.adrian99.expensesManager.services.implementation;

import com.adrian99.expensesManager.emailVerification.VerificationToken;
import com.adrian99.expensesManager.model.User;
import com.adrian99.expensesManager.repositories.UserRepository;
import com.adrian99.expensesManager.repositories.VerificationTokenRepository;
import com.adrian99.expensesManager.services.UserService;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    public UserServiceImpl(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
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
        return findAll(predicate, sort);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public String generateToken(User newUser) {

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken =
                new VerificationToken(
                        token,
                        newUser,
                        LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        return userRepository.saveAll(entities);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
