package com.example.bookmanagement

import io.r2dbc.spi.ConnectionFactory
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class BookManagementApplication

@Configuration
class DSLContextConnection(private val connectionFactory: ConnectionFactory) {
    @Bean
    fun dslContext(): DSLContext = DSL.using(connectionFactory).dsl()
}

fun main(args: Array<String>) {
    runApplication<BookManagementApplication>(*args)
}
