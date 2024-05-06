package com.example.bookmanagement.service

import org.jooq.tools.jdbc.MockDataProvider
import org.jooq.tools.jdbc.MockExecuteContext
import org.jooq.tools.jdbc.MockResult

class MyProvider : MockDataProvider {
    override fun execute(ctx: MockExecuteContext?): Array<MockResult> {
        TODO("Not yet implemented")
    }
}
