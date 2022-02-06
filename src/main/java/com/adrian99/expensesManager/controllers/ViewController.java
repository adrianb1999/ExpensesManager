package com.adrian99.expensesManager.controllers;

import com.adrian99.expensesManager.emailVerification.TokenState;
import com.adrian99.expensesManager.emailVerification.TokenType;
import com.adrian99.expensesManager.services.VerificationTokenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

    public final VerificationTokenService verificationTokenService;

    public ViewController(VerificationTokenService verificationTokenService) {
        this.verificationTokenService = verificationTokenService;
    }

    @RequestMapping(value = {"/index.html", "/", "/index"})
    public String index() {
        return "index";
    }

    @RequestMapping("/login.html")
    public String login() {
        return "login";
    }

    @RequestMapping("/createUser.html")
    public String createAccount() {
        return "createUser";
    }

    @RequestMapping("/user.html")
    public String userDashboard() {
        return "user";
    }

    @RequestMapping("/registrationConfirm.html")
    public String confirmAccount(@RequestParam(name = "token") String token, Model model) {

        TokenState tokenState = verificationTokenService.isTokenValidHtml(token, TokenType.ACCOUNT_ACTIVATION);

        if (!tokenState.equals(TokenState.VALID)) {
            model.addAttribute("TokenState", tokenState);
            return "registrationConfirm";
        }
        model.addAttribute("TokenState", "Token is valid!");
        return "login";
    }

    @RequestMapping("/passwordResetForm.html")
    public String passwordResetForm(@RequestParam(name = "token") String token, Model model) {

        TokenState tokenState = verificationTokenService.isTokenValidHtml(token,TokenType.PASSWORD_RESET);

        if (!tokenState.equals(TokenState.VALID)) {
            model.addAttribute("TokenState", tokenState);
            return "registrationConfirm";
        }

        model.addAttribute("Token", token);
        return "passwordResetForm";
    }

    @RequestMapping("/passwordReset.html")
    public String passwordReset() {
        return "passwordReset";
    }
}
