package io.github.dkuechler.jbench.controller;

import org.apache.commons.logging.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.dkuechler.jbench.login.LoggedUserManagementService;

@Controller
public class MainController {

    private final LoggedUserManagementService userService;

    public MainController(LoggedUserManagementService userService) {
        this.userService = userService;
    }

    @GetMapping("/main")
    public String home(@RequestParam(required = false) String logout, @RequestParam(required = false) String name, Model model) {
        if (logout != null) {
            userService.setLoggedUsername(null);
        }
        if (name != null) {
            userService.setLoggedUsername(name);
        }
        String username = userService.getLoggedUsername();
        if (username == null) {
            return "redirect:/";
        }
        model.addAttribute("username", username);
        return "main";
    }
    
}
