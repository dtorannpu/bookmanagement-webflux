package com.example.bookmanagement.controller.author

import com.example.bookmanagement.api.model.CreateAuthorRequest
import com.example.bookmanagement.api.model.UpdateAuthorRequest
import com.example.bookmanagement.model.Author
import com.example.bookmanagement.model.AuthorBook
import com.example.bookmanagement.service.author.AuthorService
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

@WebFluxTest(AuthorApiController::class)
class AuthorApiControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var mapper: ObjectMapper

    @MockBean
    private lateinit var authorService: AuthorService

    @Test
    fun createAuthor() =
        runTest {
            `when`(authorService.create(any(), any())).thenReturn(1)

            val request = CreateAuthorRequest("test", LocalDate.of(2000, 1, 1))
            val json = mapper.writeValueAsString(request)

            webTestClient
                .post()
                .uri("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .json("""{ "id" : 1 }""")

            verify(authorService, times(1)).create("test", LocalDate.of(2000, 1, 1))
        }

    @Test
    fun updateAuthor() =
        runTest {
            `when`(authorService.update(any(), any(), any())).thenReturn(1)

            val request = UpdateAuthorRequest(1, "test", LocalDate.of(2000, 1, 1))
            val json = mapper.writeValueAsString(request)

            webTestClient
                .patch()
                .uri("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .json("""{ "id" : 1 }""")

            verify(authorService, times(1)).update(1, "test", LocalDate.of(2000, 1, 1))
        }

    @Test
    fun updateAuthorNoUpdateTarget() =
        runTest {
            `when`(authorService.update(any(), any(), any())).thenReturn(null)

            val request = UpdateAuthorRequest(1, "test", LocalDate.of(2000, 1, 1))
            val json = mapper.writeValueAsString(request)

            webTestClient
                .patch()
                .uri("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus()
                .isOk

            verify(authorService, times(1)).update(1, "test", LocalDate.of(2000, 1, 1))
        }

    @Test
    fun getAuthorById() =
        runTest {
            `when`(authorService.findById(any())).thenReturn(
                Author(
                    1,
                    "夏目　漱石",
                    LocalDate.of(2000, 1, 1),
                    listOf(
                        AuthorBook(1, "123456", "坊ちゃん"),
                        AuthorBook(2, null, "吾輩は猫である"),
                    ),
                ),
            )

            webTestClient
                .get()
                .uri("/authors/1")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .json(
                    """
                    {
                        "id": 1,
                        "name": "夏目　漱石",
                        "birthday": "2000-01-01",
                        "books": [
                            {
                                "id": 1,
                                "isbn": "123456",
                                "title": "坊ちゃん"
                            },
                            {
                                "id": 2,
                                "isbn": null,
                                "title": "吾輩は猫である"
                            }
                        ]
                    }
                    """.trimIndent(),
                )

            verify(authorService, times(1)).findById(1)
        }

    @Test
    fun getAuthorByIdNoBook() =
        runTest {
            `when`(authorService.findById(any())).thenReturn(Author(1, "夏目　漱石", null, listOf()))

            webTestClient
                .get()
                .uri("/authors/1")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .json(
                    """
                    {
                        "id": 1,
                        "name": "夏目　漱石",
                        "birthday": null,
                        "books": []
                    }
                    """.trimIndent(),
                )

            verify(authorService, times(1)).findById(1)
        }

    @Test
    fun getAuthorByIdNotFound() =
        runTest {
            `when`(authorService.findById(any())).thenReturn(null)

            webTestClient
                .get()
                .uri("/authors/1")
                .exchange()
                .expectStatus()
                .isNotFound

            verify(authorService, times(1)).findById(1)
        }
}
