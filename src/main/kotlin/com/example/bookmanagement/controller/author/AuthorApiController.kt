package com.example.bookmanagement.controller.author

import com.example.bookmanagement.api.AuthorsApi
import com.example.bookmanagement.api.model.Author
import com.example.bookmanagement.api.model.AuthorBook
import com.example.bookmanagement.api.model.CreateAuthorRequest
import com.example.bookmanagement.api.model.CreateAuthorResponse
import com.example.bookmanagement.api.model.UpdateAuthorRequest
import com.example.bookmanagement.api.model.UpdateAuthorResponse
import com.example.bookmanagement.service.author.AuthorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * 著者コントローラー
 */
@RestController
class AuthorApiController(private val authorService: AuthorService) : AuthorsApi {
    override suspend fun createAuthor(createAuthorRequest: CreateAuthorRequest): ResponseEntity<CreateAuthorResponse> {
        val id = authorService.create(createAuthorRequest.name, createAuthorRequest.birthday)
        return ResponseEntity.ok(CreateAuthorResponse(id))
    }

    override suspend fun updateAuthor(updateAuthorRequest: UpdateAuthorRequest): ResponseEntity<UpdateAuthorResponse> {
        return authorService.update(updateAuthorRequest.id, updateAuthorRequest.name, updateAuthorRequest.birthday)?.let {
            ResponseEntity.ok(UpdateAuthorResponse(it))
        } ?: ResponseEntity.ok().build()
    }

    override suspend fun getAuthorById(id: Int): ResponseEntity<Author> {
        return authorService.findById(id)?.let { author ->
            ResponseEntity.ok(
                Author(
                    id = author.id,
                    name = author.name,
                    birthday = author.birthday,
                    books =
                        author.books.map {
                            AuthorBook(id = it.id, isbn = it.isbn, title = it.title)
                        },
                ),
            )
        } ?: ResponseEntity.notFound().build()
    }
}
