package com.internet.shop.security;

import com.internet.shop.exceptions.AuthenticationException;
import com.internet.shop.lib.Inject;
import com.internet.shop.lib.Service;
import com.internet.shop.model.User;
import com.internet.shop.service.UserService;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final String exceptionMessage = "Incorrect login or password";

    @Inject
    private UserService userService;

    @Override
    public User login(String login, String password) throws AuthenticationException {
        User user = userService.findByLogin(login).orElseThrow(() ->
                new AuthenticationException(exceptionMessage));
        if (user.getPassword().equals(password)) {
            return user;
        }
        throw new AuthenticationException(exceptionMessage);
    }
}
