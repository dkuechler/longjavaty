package io.github.dkuechler.jbench.login;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

@Service
@SessionScope
public class LoggedUserManagementService {
    private String loggedUsername;

    public String getLoggedUsername() {
        return loggedUsername;
    }

    public void setLoggedUsername(String loggedUsername) {
        this.loggedUsername = loggedUsername;
    }
}