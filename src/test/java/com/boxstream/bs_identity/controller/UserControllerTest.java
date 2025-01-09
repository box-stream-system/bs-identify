package com.boxstream.bs_identity.controller;

import com.boxstream.bs_identity.dto.request.UserCreationRequest;
import com.boxstream.bs_identity.dto.response.UserResponse;
import com.boxstream.bs_identity.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.logging.Logger;


@SpringBootTest
@AutoConfigureMockMvc // for create a request to our test function
@TestPropertySource("/test.properties") // point to test properties instead of main file .ymal
public class UserControllerTest {

    private final Logger logger = Logger.getLogger(UserControllerTest.class.getName());

    /**
     * The scope for UserControllerTest only for the controller UserController
     * Instead of calling UserService,
     * we create mock data using Mockito to fake data response from service layer
     */

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // to fake data response from UserService
    private UserService userService;

    private UserCreationRequest userCreationRequest;

    private UserResponse userResponse;

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

    }

    /**
     * This test case to test create an user success
     * Each test case always have 3 parts
     * - GIVEN: prepare input and out put data
     * - WHEN: call the api test
     * - THEN: expected result
     * @throws Exception
     */
    @Test
    void createUser_validRequest_success() throws Exception {

        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userCreationRequest);

        // this setting return userResponse when ever call userService.createNewUser()
        Mockito.when(userService.createNewUser(ArgumentMatchers.any()))
                        .thenReturn(userResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))

                .andExpect(MockMvcResultMatchers.status().isOk())  // Ok mean http status code 200
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1)) // code: 1
                .andExpect(MockMvcResultMatchers.jsonPath("data.id").value("wrewwewwef"));
                // add more andExpect here
    }

    @Test
    void createUser_usernameInvalid_fail() throws Exception {

        // GIVEN
        userCreationRequest.setUsername("john"); // set the invalid username (less than 8 characters)

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userCreationRequest);

        // this setting return userResponse when ever call userService.createNewUser()
        Mockito.when(userService.createNewUser(ArgumentMatchers.any()))
                .thenReturn(userResponse);


        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))

                .andExpect(MockMvcResultMatchers.status().isBadRequest())  // Ok mean http status code 200
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1014)) // code: 1014
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Validation failed")) // code: 1
                .andExpect(MockMvcResultMatchers.jsonPath("data[0].message").value("USERNAME_INVALID"));
        // add more andExpect here
    }

}
