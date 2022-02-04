package com.adrian99.expensesManager.services.implementation;

import com.adrian99.expensesManager.emailVerification.TokenState;
import com.adrian99.expensesManager.emailVerification.VerificationToken;
import com.adrian99.expensesManager.exception.ApiRequestException;
import com.adrian99.expensesManager.repositories.VerificationTokenRepository;
import com.adrian99.expensesManager.services.VerificationTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenServiceImpl(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public VerificationToken findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public VerificationToken findById(Long aLong) {
        return verificationTokenRepository.findById(aLong).orElse(null);
    }

    @Override
    public VerificationToken save(VerificationToken object) {
        return verificationTokenRepository.save(object);
    }

    @Override
    public void deleteById(Long aLong) {
        verificationTokenRepository.deleteById(aLong);
    }

    @Override
    public VerificationToken isTokenValid(String token) {
        VerificationToken currentToken = findByToken(token);

        if(currentToken == null)
            throw new ApiRequestException("The token is invalid!");
        if(currentToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new ApiRequestException("Token has expired!");

        return currentToken;
    }

    @Override
    public TokenState isTokenValidHtml(String token) {
        VerificationToken currentToken = findByToken(token);
        if(currentToken == null)
            return TokenState.INVALID;
        if(currentToken.getExpiryDate().isBefore(LocalDateTime.now()))
           return TokenState.EXPIRED;

        currentToken.getUser().setActive(true);
        deleteById(currentToken.getId());

        return TokenState.VALID;
    }

    @Override
    public <S extends VerificationToken> Iterable<S> saveAll(Iterable<S> entities) {
        return verificationTokenRepository.saveAll(entities);
    }
}
