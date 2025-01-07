package com.boxstream.bs_identity.configuration;

import com.boxstream.bs_identity.entity.User;
import com.boxstream.bs_identity.enums.Role;
import com.boxstream.bs_identity.repository.UserRepository;
import com.boxstream.bs_identity.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    Logger logger = LoggerFactory.getLogger(UserService.class);
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if(userRepository.findByUsername("admin").isEmpty()) {

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .tempRole(Role.ADMIN.name()) //Temp
                        .email("admin@boxstream.com")
                        .phone("123456789")
                        .firstName("admin")
                        .lastName("admin")
                        .middleName("admin")
                        .dateOfBirth("1997-07-07")
                        .build();
                userRepository.save(user);
                logger.info("Admin account is created");
            }
        };
    }
}
