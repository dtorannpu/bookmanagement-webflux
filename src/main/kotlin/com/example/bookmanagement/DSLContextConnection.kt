package com.example.bookmanagement

import io.r2dbc.spi.ConnectionFactory
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DSLContextConnection(
    private val connectionFactory: ConnectionFactory,
) {
    @Bean
    fun dslContext(): DSLContext = DSL.using(connectionFactory).dsl()
}
