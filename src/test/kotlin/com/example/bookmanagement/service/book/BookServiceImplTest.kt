package com.example.bookmanagement.service.book

import com.example.bookmanagement.TransactionCoroutineOperator
import com.example.bookmanagement.model.Book
import com.example.bookmanagement.model.BookAuthor
import com.example.bookmanagement.repository.author.AuthorRepository
import com.example.bookmanagement.repository.book.BookRepository
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BookServiceImplTest {
    @Test
    fun testCreate() =
        runTest {
            val bookRepositoryMock =
                mockk<BookRepository> {
                    coEvery { create(any(), any(), any()) } returns 2
                }
            val authorRepositoryMock =
                mockk<AuthorRepository> {
                    coEvery { existsById(1) } returns true
                }
            val provider: MockDataProvider = MyProvider()
            val connection = MockConnection(provider)
            val dslContextMock: DSLContext = DSL.using(connection, SQLDialect.POSTGRES)
            val tcoMock =
                spyk(TransactionCoroutineOperator(dslContextMock))
            val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock, tcoMock)

            val actual = bookService.create("123", 1, "坊ちゃん")
            assertEquals(2, actual)

            coVerify(exactly = 1) { authorRepositoryMock.existsById(1) }
            coVerify(exactly = 1) { bookRepositoryMock.create("123", 1, "坊ちゃん") }
        }

    @Test
    fun testCreateNoAuthor() =
        runTest {
            val bookRepositoryMock =
                mockk<BookRepository> {
                    coEvery { create(any(), any(), any()) } returns 2
                }
            val authorRepositoryMock =
                mockk<AuthorRepository> {
                    coEvery { existsById(any()) } returns false
                }
            val provider: MockDataProvider = MyProvider()
            val connection = MockConnection(provider)
            val dslContextMock: DSLContext = DSL.using(connection, SQLDialect.POSTGRES)
            val tcoMock =
                spyk(TransactionCoroutineOperator(dslContextMock))
            val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock, tcoMock)

            val actual = bookService.create("123", 1, "坊ちゃん")
            assertNull(actual)

            coVerify(exactly = 1) { authorRepositoryMock.existsById(1) }
            coVerify(exactly = 0) { bookRepositoryMock.create(any(), any(), any()) }
        }

    @Test
    fun testUpdate() =
        runTest {
            val bookRepositoryMock =
                mockk<BookRepository> {
                    coEvery { update(any(), any(), any(), any()) } returns 2
                }
            val authorRepositoryMock =
                mockk<AuthorRepository> {
                    coEvery { existsById(any()) } returns true
                }
            val provider: MockDataProvider = MyProvider()
            val connection = MockConnection(provider)
            val dslContextMock: DSLContext = DSL.using(connection, SQLDialect.POSTGRES)
            val tcoMock =
                spyk(TransactionCoroutineOperator(dslContextMock))
            val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock, tcoMock)

            val actual = bookService.update(2, "123", 1, "坊ちゃん")
            assertEquals(2, actual)

            coVerify(exactly = 1) { authorRepositoryMock.existsById(1) }
            coVerify(exactly = 1) { bookRepositoryMock.update(2, "123", 1, "坊ちゃん") }
        }

    @Test
    fun testUpdateNoAuthor() =
        runTest {
            val bookRepositoryMock =
                mockk<BookRepository> {
                    coEvery { update(any(), any(), any(), any()) } returns 2
                }
            val authorRepositoryMock =
                mockk<AuthorRepository> {
                    coEvery { existsById(any()) } returns false
                }
            val provider: MockDataProvider = MyProvider()
            val connection = MockConnection(provider)
            val dslContextMock: DSLContext = DSL.using(connection, SQLDialect.POSTGRES)
            val tcoMock =
                spyk(TransactionCoroutineOperator(dslContextMock))
            val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock, tcoMock)

            val actual = bookService.update(2, "123", 1, "坊ちゃん")
            assertNull(actual)

            coVerify(exactly = 1) { authorRepositoryMock.existsById(1) }
            coVerify(exactly = 0) { bookRepositoryMock.create(any(), any(), any()) }
        }

    @Test
    fun testUpdateNoUpdateTarget() =
        runTest {
            val bookRepositoryMock =
                mockk<BookRepository> {
                    coEvery { update(any(), any(), any(), any()) } returns 0
                }
            val authorRepositoryMock =
                mockk<AuthorRepository> {
                    coEvery { existsById(any()) } returns true
                }
            val provider: MockDataProvider = MyProvider()
            val connection = MockConnection(provider)
            val dslContextMock: DSLContext = DSL.using(connection, SQLDialect.POSTGRES)
            val tcoMock =
                spyk(TransactionCoroutineOperator(dslContextMock))
            val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock, tcoMock)

            val actual = bookService.update(2, "123", 1, "坊ちゃん")
            assertNull(actual)

            coVerify(exactly = 1) { authorRepositoryMock.existsById(1) }
            coVerify(exactly = 1) { bookRepositoryMock.update(2, "123", 1, "坊ちゃん") }
        }

    @Test
    fun testSearch() =
        runTest {
            val bookRepositoryMock =
                mockk<BookRepository> {
                    coEvery { search(any(), any(), any()) } returns listOf(Book(1, "123", "坊ちゃん", BookAuthor(1, "夏目　漱石", null)))
                }
            val authorRepositoryMock =
                mockk<AuthorRepository> {}
            val provider: MockDataProvider = MyProvider()
            val connection = MockConnection(provider)
            val dslContextMock: DSLContext = DSL.using(connection, SQLDialect.POSTGRES)
            val tcoMock =
                spyk(TransactionCoroutineOperator(dslContextMock))

            val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock, tcoMock)

            val actual = bookService.search("坊ちゃん", "夏目　漱石", "123")
            assertEquals(1, actual.size)
            coVerify(exactly = 1) { bookRepositoryMock.search("坊ちゃん", "夏目　漱石", "123") }
        }
}
