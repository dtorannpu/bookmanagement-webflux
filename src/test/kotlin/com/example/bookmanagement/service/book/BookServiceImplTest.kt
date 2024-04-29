package com.example.bookmanagement.service.book

import com.example.bookmanagement.model.Book
import com.example.bookmanagement.model.BookAuthor
import com.example.bookmanagement.repository.author.AuthorRepository
import com.example.bookmanagement.repository.book.BookRepository
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BookServiceImplTest {
    @Test
    fun testCreate() {
        val bookRepositoryMock =
            mock<BookRepository> {
                on { create(any(), any(), any()) } doReturn 2
            }
        val authorRepositoryMock =
            mock<AuthorRepository> {
                on { existsById(1) } doReturn true
            }
        val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock)

        val actual = bookService.create("123", 1, "坊ちゃん")
        assertEquals(2, actual)

        verify(authorRepositoryMock, times(1)).existsById(1)
        verify(bookRepositoryMock, times(1)).create("123", 1, "坊ちゃん")
    }

    @Test
    fun testCreateNoAuthor() {
        val bookRepositoryMock =
            mock<BookRepository> {
                on { create(any(), any(), any()) } doReturn 2
            }
        val authorRepositoryMock =
            mock<AuthorRepository> {
                on { existsById(any()) } doReturn false
            }
        val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock)

        val actual = bookService.create("123", 1, "坊ちゃん")
        assertNull(actual)

        verify(authorRepositoryMock, times(1)).existsById(1)
        verify(bookRepositoryMock, never()).create(any(), any(), any())
    }

    @Test
    fun testUpdate() {
        val bookRepositoryMock =
            mock<BookRepository> {
                on { update(any(), any(), any(), any()) } doReturn 2
            }
        val authorRepositoryMock =
            mock<AuthorRepository> {
                on { existsById(any()) } doReturn true
            }
        val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock)

        val actual = bookService.update(2, "123", 1, "坊ちゃん")
        assertEquals(2, actual)

        verify(authorRepositoryMock, times(1)).existsById(1)
        verify(bookRepositoryMock, times(1)).update(2, "123", 1, "坊ちゃん")
    }

    @Test
    fun testUpdateNoAuthor() {
        val bookRepositoryMock =
            mock<BookRepository> {
                on { update(any(), any(), any(), any()) } doReturn 2
            }
        val authorRepositoryMock =
            mock<AuthorRepository> {
                on { existsById(any()) } doReturn false
            }
        val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock)

        val actual = bookService.update(2, "123", 1, "坊ちゃん")
        assertNull(actual)

        verify(authorRepositoryMock, times(1)).existsById(1)
        verify(bookRepositoryMock, never()).create(any(), any(), any())
    }

    @Test
    fun testUpdateNoUpdateTarget() {
        val bookRepositoryMock =
            mock<BookRepository> {
                on { update(any(), any(), any(), any()) } doReturn 0
            }
        val authorRepositoryMock =
            mock<AuthorRepository> {
                on { existsById(any()) } doReturn true
            }
        val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock)

        val actual = bookService.update(2, "123", 1, "坊ちゃん")
        assertNull(actual)

        verify(authorRepositoryMock, times(1)).existsById(1)
        verify(bookRepositoryMock, times(1)).update(2, "123", 1, "坊ちゃん")
    }

    @Test
    fun testSearch() {
        val bookRepositoryMock =
            mock<BookRepository> {
                on { search(any(), any(), any()) } doReturn listOf(Book(1, "123", "坊ちゃん", BookAuthor(1, "夏目　漱石", null)))
            }
        val authorRepositoryMock =
            mock<AuthorRepository> {}

        val bookService = BookServiceImpl(bookRepositoryMock, authorRepositoryMock)

        val actual = bookService.search("坊ちゃん", "夏目　漱石", "123")
        assertEquals(1, actual.size)
        verify(bookRepositoryMock, times(1)).search("坊ちゃん", "夏目　漱石", "123")
    }
}
