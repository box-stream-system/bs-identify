package com.boxstream.bs_identity.configuration;

import com.boxstream.bs_identity.entity.Role;
import com.boxstream.bs_identity.entity.User;
import com.boxstream.bs_identity.repository.UserRepository;
import com.boxstream.bs_identity.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    Logger logger = LoggerFactory.getLogger(UserService.class);
    PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    /**
     * @ConditionalOnProperty
     * If run test cases
     * It's will use H2 Database
     * Then userRepository.findByUsername("admin") will error
     * then this config to make sure this applicationRunner() only run when not run test case
     */
    ApplicationRunner applicationRunner(UserRepository userRepository) {


        return args -> {
            if(userRepository.findByUsername("admin").isEmpty()) {
                Role roleAdmin = new Role();
                roleAdmin.setName("ADMIN");

                Set<Role> roles = new HashSet<>();
                roles.add(roleAdmin);

                LocalDate dateOfBirth2000 = LocalDate.of(2000, 1, 1);

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .email("admin@boxstream.com")
                        .phone("123456789")
                        .firstName("admin")
                        .lastName("admin")
                        .middleName("admin")
                        .dateOfBirth(dateOfBirth2000)
                        .build();
                userRepository.save(user);
                logger.info("Admin account is created");
            }
        };
    }
}
