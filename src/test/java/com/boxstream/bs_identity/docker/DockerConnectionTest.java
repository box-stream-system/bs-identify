package com.boxstream.bs_identity.docker;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DockerConnectionTest {

    @Test
    void testMySQLContainer() {
        try (MySQLContainer<?> mysql = new MySQLContainer<>("mysql:5.7")) {
            mysql.start();
            assertTrue(mysql.isRunning());
        }
    }
}
