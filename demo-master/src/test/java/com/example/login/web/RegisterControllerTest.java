package com.example.login.web;

import com.example.login.model.User;
import com.example.login.model.UserDTO;
import com.example.login.repository.UserRepository;
import com.example.login.service.AdminDetailsService;
import com.example.login.service.MyUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminDetailsService adminDetailsService;

    @Mock
    private MyUserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterController registerController;

    private MockMvc mockMvc;

    @Test
    public void testShowRegisterForm() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(registerController).build();

        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register_form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(registerController).build();

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password");

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .flashAttr("user", userDTO))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register_success"));

        // Verify userRepository.save() is called with correct User object
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .flashAttr("user", userDTO))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register_success"));
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(registerController).build();

        User existingUser = new User();
        existingUser.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("existing@example.com");
        userDTO.setPassword("password");

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .flashAttr("user", userDTO))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register_form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("emailError"));
    }
}