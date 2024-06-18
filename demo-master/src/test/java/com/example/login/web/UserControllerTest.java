package com.example.login.web;

import com.example.login.model.User;
import com.example.login.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegisterController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testSuccessfulUserRemoval() throws Exception {
        // Arrange
        User userToDelete = new User();
        userToDelete.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(userToDelete);

        // Act and Assert
        mockMvc.perform(delete("/delete")
                        .param("email", "user@example.com")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/delete_success"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testUnsuccessfulUserRemoval_UserNotFound() throws Exception {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        // Act and Assert
        mockMvc.perform(delete("/delete")
                        .param("email", "nonexistentuser@example.com")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("delete_error"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "User not found"));
    }
}