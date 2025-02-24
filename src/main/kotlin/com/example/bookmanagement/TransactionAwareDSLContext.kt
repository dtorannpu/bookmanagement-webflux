package com.example.bookmanagement

import org.jooq.DSLContext
import org.springframework.stereotype.Component
import kotlin.coroutines.coroutineContext

@Component
class TransactionAwareDSLContext(
    private val dslContext: DSLContext,
) {
    suspend fun get(): DSLContext = coroutineContext.getDSLContext() ?: this.dslContext
}
