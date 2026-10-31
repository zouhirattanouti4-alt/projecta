package com.zouhir.neobank.integrationTests;

import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

class DockerSetupIT {

    @Test
    void postgresContainerShouldStart() {
        try (PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16")) {
            postgres.start();
            assertThat(postgres.isRunning()).isTrue();
            System.out.println("JDBC URL : " + postgres.getJdbcUrl());
        }
    }
}