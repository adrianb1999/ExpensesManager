package com.adrian99.expensesManager.services.implementation;

import com.adrian99.expensesManager.auth.ApplicationUser;
import com.adrian99.expensesManager.exception.ApiRequestException;
import com.adrian99.expensesManager.model.User;
import com.adrian99.expensesManager.services.UserService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserService implements UserDetailsService {

    UserService userService;

    public ApplicationUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username not found!");
        }
        return new ApplicationUser(user);
    }
}
