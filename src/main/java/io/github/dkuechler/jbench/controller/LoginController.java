package io.github.dkuechler.jbench.controller;

import io.github.dkuechler.jbench.login.LoginProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private LoginProcessor loginProcessor;

    @GetMapping("/")
    public String loginGet() {
        return "login";
    }

    @PostMapping("/")
    public String loginPost(@RequestParam String username, @RequestParam String password, Model model) {
        loginProcessor.setUsername(username);
        loginProcessor.setPassword(password);
        var loggedIn = loginProcessor.login();

        if (loggedIn) {
            model.addAttribute("message", "Login successful!");
        } else {
            model.addAttribute("message", "Login failed. Please try again.");
        }
        return "login";
    }
}
