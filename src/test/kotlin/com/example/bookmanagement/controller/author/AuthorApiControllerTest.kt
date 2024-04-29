package com.example.bookmanagement.controller.author

import com.example.bookmanagement.api.model.CreateAuthorRequest
import com.example.bookmanagement.api.model.UpdateAuthorRequest
import com.example.bookmanagement.model.Author
import com.example.bookmanagement.model.AuthorBook
import com.example.bookmanagement.service.author.AuthorService
import com.fasterxml.jackson.databind.ObjectMapper
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import java.time.LocalDate
import kotlin.test.Test

@WebMvcTest(AuthorApiController::class)
class AuthorApiControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @MockBean
    private lateinit var authorService: AuthorService

    @Test
    fun createAuthor() {
        `when`(authorService.create(any(), any())).thenReturn(1)

        val request = CreateAuthorRequest("test", LocalDate.of(2000, 1, 1))
        val json = mapper.writeValueAsString(request)

        mockMvc.post("/authors") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            content { json("""{ "id" : 1 }""") }
        }

        verify(authorService, times(1)).create("test", LocalDate.of(2000, 1, 1))
    }

    @Test
    fun updateAuthor() {
        `when`(authorService.update(any(), any(), any())).thenReturn(1)

        val request = UpdateAuthorRequest(1, "test", LocalDate.of(2000, 1, 1))
        val json = mapper.writeValueAsString(request)

        mockMvc.patch("/authors") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            content {
                json("""{ "id" : 1 }""")
            }
        }

        verify(authorService, times(1)).update(1, "test", LocalDate.of(2000, 1, 1))
    }

    @Test
    fun updateAuthorNoUpdateTarget() {
        `when`(authorService.update(any(), any(), any())).thenReturn(null)

        val request = UpdateAuthorRequest(1, "test", LocalDate.of(2000, 1, 1))
        val json = mapper.writeValueAsString(request)

        mockMvc.patch("/authors") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect { status { isOk() } }

        verify(authorService, times(1)).update(1, "test", LocalDate.of(2000, 1, 1))
    }

    @Test
    fun getAuthorById() {
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

        mockMvc.get("/authors/1")
            .andExpect {
                status { isOk() }
                content {
                    json(
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
                }
            }

        verify(authorService, times(1)).findById(1)
    }

    @Test
    fun getAuthorByIdNoBook() {
        `when`(authorService.findById(any())).thenReturn(Author(1, "夏目　漱石", null, listOf()))

        mockMvc.get("/authors/1")
            .andExpect {
                status { isOk() }
                content {
                    json(
                        """
                        {
                            "id": 1,
                            "name": "夏目　漱石",
                            "birthday": null,
                            "books": []
                        }
                        """.trimIndent(),
                    )
                }
            }

        verify(authorService, times(1)).findById(1)
    }

    @Test
    fun getAuthorByIdNotFound() {
        `when`(authorService.findById(any())).thenReturn(null)

        mockMvc.get("/authors/1")
            .andExpect { status { isNotFound() } }

        verify(authorService, times(1)).findById(1)
    }
}
