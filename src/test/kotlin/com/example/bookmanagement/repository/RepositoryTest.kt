package com.example.bookmanagement.repository

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

abstract class RepositoryTest {
    companion object {
        val db = PostgreSQLContainer("postgres:16.2")

        init {
            db.start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerDBContainer(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)

            registry.add(
                "spring.r2dbc.url",
            ) { String.format("r2dbc:pool:postgresql://%s:%d/%s", db.host, db.firstMappedPort, db.databaseName) }
            registry.add("spring.r2dbc.username", db::getUsername)
            registry.add("spring.r2dbc.password", db::getPassword)
        }
    }
}
