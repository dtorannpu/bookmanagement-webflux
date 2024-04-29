package com.example.bookmanagement.controller.author

import com.example.bookmanagement.api.AuthorsApi
import com.example.bookmanagement.api.model.Author
import com.example.bookmanagement.api.model.AuthorBook
import com.example.bookmanagement.api.model.CreateAuthorRequest
import com.example.bookmanagement.api.model.CreateAuthorResponse
import com.example.bookmanagement.api.model.UpdateAuthorRequest
import com.example.bookmanagement.api.model.UpdateAuthorResponse
import com.example.bookmanagement.service.author.AuthorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * 著者コントローラー
 */
@RestController
class AuthorApiController(private val authorService: AuthorService) : AuthorsApi {
    override fun createAuthor(createAuthorRequest: CreateAuthorRequest): ResponseEntity<CreateAuthorResponse> {
        val id = authorService.create(createAuthorRequest.name, createAuthorRequest.birthday)
        return ResponseEntity.ok(CreateAuthorResponse(id))
    }

    override fun updateAuthor(updateAuthorRequest: UpdateAuthorRequest): ResponseEntity<UpdateAuthorResponse> {
        val id =
            authorService.update(updateAuthorRequest.id, updateAuthorRequest.name, updateAuthorRequest.birthday)
                ?: return ResponseEntity(HttpStatus.OK)
        return ResponseEntity.ok(UpdateAuthorResponse(id))
    }

    override fun getAuthorById(id: Int): ResponseEntity<Author> {
        val author = authorService.findById(id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity.ok(
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
    }
}
