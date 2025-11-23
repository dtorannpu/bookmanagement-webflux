package com.example.bookmanagement.service.author

import com.example.bookmanagement.TransactionCoroutineOperator
import com.example.bookmanagement.model.Author
import com.example.bookmanagement.repository.author.AuthorRepository
import com.example.bookmanagement.service.MyProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.tools.jdbc.MockConnection
import org.jooq.tools.jdbc.MockDataProvider
import org.junit.jupiter.api.Assertions.assertNotNull
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AuthorServiceImplTest {
    @Test
    fun testCreate() =
        runTest {
            val mock =
                mockk<AuthorRepository> {
                    coEvery { create(any(), any()) } returns 1
                }
            val provider: MockDataProvider = MyProvider()
            val connection = MockConnection(provider)
            val dslContextMock: DSLContext = DSL.using(connection, SQLDialect.POSTGRES)
            val tcoMock =
                spyk(TransactionCoroutineOperator(dslContextMock))
            val authorService = AuthorServiceImpl(mock, tcoMock)

            val actual = authorService.create("山田　太郎", LocalDate.of(2000, 1, 1))
            assertEquals(1, actual)

            coVerify(exactly = 1) {
                mock.create(
                    "山田　太郎",
                    LocalDate.of(2000, 1, 1),
                )
            }
        }

    @Test
    fun testUpdate() =
        runTest {
            val mock =
                mockk<AuthorRepository> {
                    coEvery { update(any(), any(), any()) } returns 1
                }
            val provider: MockDataProvider = MyProvider()
            val connection = MockConnection(provider)
            val dslContextMock: DSLContext = DSL.using(connection, SQLDialect.POSTGRES)
            val tcoMock =
                spyk(TransactionCoroutineOperator(dslContextMock))
            val authorService = AuthorServiceImpl(mock, tcoMock)

            val actual = authorService.update(1, "山田　太郎", LocalDate.of(2000, 1, 1))
            assertEquals(1, actual)

            coVerify(exactly = 1) {
                mock.update(
                    1,
                    "山田　太郎",
                    LocalDate.of(2000, 1, 1),
                )
            }
        }

    @Test
    fun testUpdateNoUpdateTarget() =
        runTest {
            val mock =
                mockk<AuthorRepository> {
                    coEvery { update(any(), any(), any()) } returns 0
                }
            val provider: MockDataProvider = MyProvider()
            val connection = MockConnection(provider)
            val dslContextMock: DSLContext = DSL.using(connection, SQLDialect.POSTGRES)
            val tcoMock =
                spyk(TransactionCoroutineOperator(dslContextMock))
            val authorService = AuthorServiceImpl(mock, tcoMock)

            val actual = authorService.update(2, "山田　太郎", LocalDate.of(2000, 1, 1))
            assertNull(actual)

            coVerify(exactly = 1) {
                mock.update(
                    2,
                    "山田　太郎",
                    LocalDate.of(2000, 1, 1),
                )
            }
        }

    @Test
    fun testSearch() =
        runTest {
            val mock =
                mockk<AuthorRepository> {
                    coEvery { findById(any()) } returns Author(1, "山田太郎", null, listOf())
                }
            val provider: MockDataProvider = MyProvider()
            val connection = MockConnection(provider)
            val dslContextMock: DSLContext = DSL.using(connection, SQLDialect.POSTGRES)
            val tcoMock =
                spyk(TransactionCoroutineOperator(dslContextMock))
            val authorService = AuthorServiceImpl(mock, tcoMock)

            val actual = authorService.findById(1)
            assertNotNull(actual)

            coVerify(exactly = 1) { mock.findById(1) }
        }
}
