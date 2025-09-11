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

    private final LoginProcessor loginProcessor;

    @Autowired
    public LoginController(LoginProcessor loginProcessor) {
        this.loginProcessor = loginProcessor;
    }

    @GetMapping("/")
    public String loginGet() {
        return "login";
    }

    @PostMapping("/")
    public String loginPost(@RequestParam String username, @RequestParam String password, Model model) {
        var loggedIn = loginProcessor.login(username, password);

        if (loggedIn) {
            return "redirect:/main?name=" + username;
        } else {
        }
            model.addAttribute("message", "Login failed. Please try again.");
        return "login";
    }
}
