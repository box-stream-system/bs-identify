package com.boxstream.bs_identity.controller;

import com.boxstream.bs_identity.dto.request.UserCreationRequest;
import com.boxstream.bs_identity.dto.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;


@SpringBootTest
@AutoConfigureMockMvc // for create a request to our test function
@Testcontainers
public class UserControllerIntegationTest {

    /**
     * The scope for UserControllerTest only for the controller UserController
     * Instead of calling UserService,
     * we create mock data using Mockito to fake data response from service layer
     */

    @Container
    static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:5.7");


    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private MockMvc mockMvc;


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



        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))

                .andExpect(MockMvcResultMatchers.status().isOk())  // Ok mean http status code 200
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1)); // code: 1
                // add more andExpect here
    }


}
