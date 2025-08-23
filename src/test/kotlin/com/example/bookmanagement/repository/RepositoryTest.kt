package com.example.bookmanagement.repository

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeAll
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

abstract class RepositoryTest {
    companion object {
        private val db = PostgreSQLContainer("postgres:17.6")

        init {
            db.start()
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            Flyway
                .configure()
                .dataSource(db.jdbcUrl, db.username, db.password)
                .load()
                .migrate()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerDBContainer(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)

            registry.add(
                "spring.r2dbc.url",
            ) { String.format("r2dbc:postgresql://%s:%d/%s", db.host, db.firstMappedPort, db.databaseName) }
            registry.add("spring.r2dbc.username", db::getUsername)
            registry.add("spring.r2dbc.password", db::getPassword)
            registry.add("spring.r2dbc.pool.enabled") { true }
        }
    }
}
