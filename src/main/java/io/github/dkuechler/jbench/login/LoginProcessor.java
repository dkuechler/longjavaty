package io.github.dkuechler.jbench.login;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class LoginProcessor {

    private final LoggedUserManagementService userService;

    public LoginProcessor(LoggedUserManagementService userService) {
        this.userService = userService;
    }

    public boolean login(String username, String password) {
        // TODO implement actual login logic
        return "John".equals(username) && "password".equals(password);
    }
    
}
