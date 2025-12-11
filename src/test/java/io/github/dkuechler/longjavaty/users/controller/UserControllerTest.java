package io.github.dkuechler.longjavaty.users.controller;

import io.github.dkuechler.longjavaty.users.controller.dto.UserDataExport;
import io.github.dkuechler.longjavaty.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void exportMyData_shouldReturnAllUserData() throws Exception {
        // Given: Mock user data export
        UUID userId = UUID.randomUUID();
        UserDataExport.UserInfo userInfo = new UserDataExport.UserInfo(
                userId, "test@example.com", OffsetDateTime.now());
        
        UserDataExport export = new UserDataExport(
                userInfo, 
                List.of(), // workouts
                List.of(), // measurements
                OffsetDateTime.now()
        );
        
        when(userService.exportUserData(any(UUID.class))).thenReturn(export);

        // When & Then: Export data succeeds
        mockMvc.perform(get("/api/users/me/data")
                        .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(userId.toString()))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.workouts").isArray())
                .andExpect(jsonPath("$.measurements").isArray())
                .andExpect(jsonPath("$.exportedAt").exists());
    }

    @Test
    void exportMyData_userNotFound_shouldReturn404() throws Exception {
        // Given: User doesn't exist
        UUID userId = UUID.randomUUID();
        when(userService.exportUserData(any(UUID.class)))
                .thenThrow(new NoSuchElementException("User not found"));

        // When & Then: Returns 404
        mockMvc.perform(get("/api/users/me/data")
                        .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMyAccount_shouldDeleteUserAndAllData() throws Exception {
        // Given: User exists and can be deleted
        UUID userId = UUID.randomUUID();
        when(userService.deleteUserAndAllData(any(UUID.class))).thenReturn(true);

        // When & Then: Deletion succeeds
        mockMvc.perform(delete("/api/users/me/data")
                        .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMyAccount_nonExistentUser_shouldReturn404() throws Exception {
        // Given: User doesn't exist
        UUID userId = UUID.randomUUID();
        when(userService.deleteUserAndAllData(any(UUID.class))).thenReturn(false);

        // When & Then: Returns 404
        mockMvc.perform(delete("/api/users/me/data")
                        .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                .andExpect(status().isNotFound());
    }
}
