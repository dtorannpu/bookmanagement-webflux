package com.example.bookmanagement.controller.book

import com.example.bookmanagement.api.model.CreateBookRequest
import com.example.bookmanagement.api.model.UpdateBookRequest
import com.example.bookmanagement.model.Book
import com.example.bookmanagement.model.BookAuthor
import com.example.bookmanagement.service.book.BookService
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import kotlin.test.Test

@WebFluxTest(BookApiController::class)
class BookApiControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var mapper: ObjectMapper

    @MockBean
    private lateinit var bookService: BookService

    @Test
    fun createBook() =
        runTest {
            `when`(bookService.create(any(), any(), any())).thenReturn(1)

            val request = CreateBookRequest(2, "こころ", "1234567890")
            val json = mapper.writeValueAsString(request)

            webTestClient
                .post()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .json("""{ "id" : 1 }""")

            verify(bookService, times(1)).create("1234567890", 2, "こころ")
        }

    @Test
    fun createBookNoAuthor() =
        runTest {
            `when`(bookService.create(any(), any(), any())).thenReturn(null)

            val request = CreateBookRequest(2, "こころ", "1234567890")
            val json = mapper.writeValueAsString(request)

            webTestClient
                .post()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .isEmpty

            verify(bookService, times(1)).create("1234567890", 2, "こころ")
        }

    @Test
    fun updateBook() =
        runTest {
            `when`(bookService.update(any(), any(), any(), any())).thenReturn(1)

            val request = UpdateBookRequest(1, 2, "こころ", "1234567890")
            val json = mapper.writeValueAsString(request)

            webTestClient
                .patch()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .json("""{ "id" : 1 }""")

            verify(bookService, times(1)).update(1, "1234567890", 2, "こころ")
        }

    @Test
    fun updateBookNoAuthor() =
        runTest {
            `when`(bookService.update(any(), any(), any(), any())).thenReturn(null)

            val request = UpdateBookRequest(1, 2, "こころ", "1234567890")
            val json = mapper.writeValueAsString(request)

            webTestClient
                .patch()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .isEmpty

            verify(bookService, times(1)).update(1, "1234567890", 2, "こころ")
        }

    @Test
    fun searchBook() =
        runTest {
            `when`(bookService.search(any(), any(), any())).thenReturn(
                listOf(
                    Book(1, "1234567890", "こころ", BookAuthor(1, "夏目　漱石", LocalDate.of(2000, 1, 1))),
                    Book(2, "1234567890", "こころ改訂版", BookAuthor(1, "夏目　漱石", LocalDate.of(2000, 1, 1))),
                ),
            )

            webTestClient
                .get()
                .uri("/books?bookTitle=こころ&isbn=1234567890&authorName=夏目　漱石")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .json(
                    """
                    [
                        {
                            "id": 1,
                            "isbn": "1234567890",
                            "title": "こころ",
                            "author": {
                                "authorId": 1,
                                "name": "夏目　漱石",
                                "birthday": "2000-01-01"
                            }
                        },
                        {
                            "id": 2,
                            "isbn": "1234567890",
                            "title": "こころ改訂版",
                            "author": {
                                "authorId": 1,
                                "name": "夏目　漱石",
                                "birthday": "2000-01-01"
                            }
                        }
                    ]
                    """.trimIndent(),
                )

            verify(bookService, times(1)).search("こころ", "夏目　漱石", "1234567890")
        }
}
