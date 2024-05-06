package com.example.bookmanagement

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.kotlin.coroutines.transactionCoroutine
import org.springframework.stereotype.Component
import kotlin.coroutines.coroutineContext

@Component
class TransactionCoroutineOperator(private val dslContext: DSLContext) {
    suspend fun <T> execute(block: suspend CoroutineScope.() -> T): T {
        val propagatedDSLContext = coroutineContext.getDSLContext()
        return if (propagatedDSLContext != null) {
            execute(propagatedDSLContext, block)
        } else {
            execute(dslContext, block)
        }
    }

    private suspend fun <T> execute(
        dslContext: DSLContext,
        block: suspend CoroutineScope.() -> T,
    ): T = dslContext.transactionCoroutine { config -> withContext(coroutineContext.addDSLContext(config.dsl()), block) }
}
