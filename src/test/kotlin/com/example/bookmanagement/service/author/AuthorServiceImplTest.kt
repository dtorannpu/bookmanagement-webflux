package com.example.bookmanagement.service.author

import com.example.bookmanagement.model.Author
import com.example.bookmanagement.repository.author.AuthorRepository
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthorServiceImplTest {
    @Test
    fun testCreate() {
        val mock =
            mock<AuthorRepository> {
                on { create(any(), any()) } doReturn 1
            }
        val authorService = AuthorServiceImpl(mock)

        val actual = authorService.create("山田　太郎", LocalDate.of(2000, 1, 1))
        assertEquals(1, actual)

        verify(mock, times(1)).create(
            "山田　太郎",
            LocalDate.of(2000, 1, 1),
        )
    }

    @Test
    fun testUpdate() {
        val mock =
            mock<AuthorRepository> {
                on { update(any(), any(), any()) } doReturn 1
            }
        val authorService = AuthorServiceImpl(mock)

        val actual = authorService.update(1, "山田　太郎", LocalDate.of(2000, 1, 1))
        assertEquals(1, actual)

        verify(mock, times(1)).update(
            1,
            "山田　太郎",
            LocalDate.of(2000, 1, 1),
        )
    }

    @Test
    fun testUpdateNoUpdateTarget() {
        val mock =
            mock<AuthorRepository> {
                on { update(any(), any(), any()) } doReturn 0
            }
        val authorService = AuthorServiceImpl(mock)

        val actual = authorService.update(2, "山田　太郎", LocalDate.of(2000, 1, 1))
        assertNull(actual)

        verify(mock, times(1)).update(
            2,
            "山田　太郎",
            LocalDate.of(2000, 1, 1),
        )
    }

    @Test
    fun testSearch() {
        val mock =
            mock<AuthorRepository> {
                on { findById(any()) } doReturn Author(1, "山田太郎", null, listOf())
            }
        val authorService = AuthorServiceImpl(mock)

        val actual = authorService.findById(1)
        assertNotNull(actual)

        verify(mock, times(1)).findById(1)
    }
}
