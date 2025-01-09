package com.boxstream.bs_identity.service;

import com.boxstream.bs_identity.dto.request.UserCreationRequest;
import com.boxstream.bs_identity.dto.response.UserResponse;
import com.boxstream.bs_identity.entity.User;
import com.boxstream.bs_identity.exception.ApplicationException;
import com.boxstream.bs_identity.exception.UserNotFoundException;
import com.boxstream.bs_identity.exception.UsernameExistsException;
import com.boxstream.bs_identity.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc // for create a request to our test function
@TestPropertySource("/test.properties") // point to test properties instead of main file .ymal
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    private UserCreationRequest userCreationRequest;

    private UserResponse userResponse;

    private User user;

    @BeforeEach
    void initializeData() {
        userCreationRequest = UserCreationRequest.builder()
                .email("johnwick@gmail.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .firstName("John")
                .lastName("Wick")
                .middleName("D")
                .phone("+84 961463999")
                .username("johnwick97")
                .password("John@Wick97")
                .build();

        userResponse = UserResponse.builder()
                .id("wrewwewwef") // fake id response
                .email("johnwick@gmail.com")
                .dateOfBirth("1990-1-1")
                .firstName("John")
                .lastName("Wick")
                .middleName("D")
                .phone("+84 961463999")
                .username("johnwick97")
                .build();

        user = User.builder()
                .id("wrewwewwef") // fake id response
                .email("johnwick@gmail.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .firstName("John")
                .lastName("Wick")
                .middleName("D")
                .phone("+84 961463999")
                .username("johnwick97")
                .build();


    }

    @Test
    void createUser_validRequest_success() {

        // GIVEN: make deal
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        // WHEN
        var reponse = userService.createNewUser(userCreationRequest);

        // THEN
        Assertions.assertNotNull(reponse);
        Assertions.assertEquals(userResponse.getFirstName(), reponse.getFirstName());
        Assertions.assertEquals(userResponse.getLastName(), reponse.getLastName());
        Assertions.assertEquals(userResponse.getEmail(), reponse.getEmail());
        Assertions.assertEquals(userResponse.getPhone(), reponse.getPhone());
        Assertions.assertEquals(userResponse.getUsername(), reponse.getUsername());
        Assertions.assertEquals(userResponse.getId(), reponse.getId());

    }

    @Test
    void createUser_userExists_success() {

        // GIVEN: make deal
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // WHEN
        var exception = Assertions
                .assertThrows(UsernameExistsException.class, () -> userService.createNewUser(userCreationRequest));

        // THEN
        Assertions.assertEquals(exception.getErrorCode().getCode(), 1004);
        Assertions.assertEquals(exception.getMessage(), "Username already exists");
    }

    @Test
    @WithMockUser(username = "johnwick97")
    void getMyinfo_validRequest_success() {

        // GIVEN
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // WHEN
        var response = userService.getCurrentUserInfo();

        // THEN
        Assertions.assertNotNull(response);
        Assertions.assertEquals(userResponse.getUsername(), response.getUsername());
        Assertions.assertEquals(userResponse.getFirstName(), response.getFirstName());
        Assertions.assertEquals(userResponse.getLastName(), response.getLastName());
        Assertions.assertEquals(userResponse.getEmail(), response.getEmail());
        Assertions.assertEquals(userResponse.getPhone(), response.getPhone());
        Assertions.assertEquals(userResponse.getId(), response.getId());

    }

}
