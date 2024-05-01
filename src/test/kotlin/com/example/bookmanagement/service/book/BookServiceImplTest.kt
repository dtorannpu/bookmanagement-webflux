package com.example.bookmanagement.service.book

import com.example.bookmanagement.model.Book
import com.example.bookmanagement.model.BookAuthor
import com.example.bookmanagement.repository.author.AuthorRepository
import com.example.bookmanagement.repository.book.BookRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.never
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
            val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock)

            val actual = bookService.create("123", 1, "坊ちゃん")
            assertEquals(2, actual)

            verify(authorRepositoryMock, times(1)).existsById(1)
            verify(bookRepositoryMock, times(1)).create("123", 1, "坊ちゃん")
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
            val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock)

            val actual = bookService.create("123", 1, "坊ちゃん")
            assertNull(actual)

            verify(authorRepositoryMock, times(1)).existsById(1)
            verify(bookRepositoryMock, never()).create(any(), any(), any())
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
            val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock)

            val actual = bookService.update(2, "123", 1, "坊ちゃん")
            assertEquals(2, actual)

            verify(authorRepositoryMock, times(1)).existsById(1)
            verify(bookRepositoryMock, times(1)).update(2, "123", 1, "坊ちゃん")
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
            val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock)

            val actual = bookService.update(2, "123", 1, "坊ちゃん")
            assertNull(actual)

            verify(authorRepositoryMock, times(1)).existsById(1)
            verify(bookRepositoryMock, never()).create(any(), any(), any())
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
            val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock)

            val actual = bookService.update(2, "123", 1, "坊ちゃん")
            assertNull(actual)

            verify(authorRepositoryMock, times(1)).existsById(1)
            verify(bookRepositoryMock, times(1)).update(2, "123", 1, "坊ちゃん")
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

            val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock)

            val actual = bookService.search("坊ちゃん", "夏目　漱石", "123")
            assertEquals(1, actual.size)
            verify(bookRepositoryMock, times(1)).search("坊ちゃん", "夏目　漱石", "123")
        }
}
