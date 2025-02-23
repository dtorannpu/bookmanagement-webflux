package com.example.bookmanagement.controller.book

import com.example.bookmanagement.api.BooksApi
import com.example.bookmanagement.api.model.Book
import com.example.bookmanagement.api.model.BookAuthor
import com.example.bookmanagement.api.model.CreateBookRequest
import com.example.bookmanagement.api.model.CreateBookResponse
import com.example.bookmanagement.api.model.UpdateBookRequest
import com.example.bookmanagement.api.model.UpdateBookResponse
import com.example.bookmanagement.service.book.BookService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * 書籍コントローラー
 */
@RestController
class BookApiController(
    private val bookService: BookService,
) : BooksApi {
    override suspend fun createBook(createBookRequest: CreateBookRequest): ResponseEntity<CreateBookResponse> =
        bookService.create(createBookRequest.isbn, createBookRequest.authorId, createBookRequest.title)?.let {
            ResponseEntity.ok(CreateBookResponse(it))
        } ?: ResponseEntity.ok().build()

    override suspend fun updateBook(updateBookRequest: UpdateBookRequest): ResponseEntity<UpdateBookResponse> =
        bookService.update(updateBookRequest.id, updateBookRequest.isbn, updateBookRequest.authorId, updateBookRequest.title)?.let {
            ResponseEntity.ok(UpdateBookResponse(it))
        } ?: ResponseEntity.ok().build()

    override fun searchBook(
        bookTitle: String?,
        authorName: String?,
        isbn: String?,
    ): ResponseEntity<Flow<Book>> =
        ResponseEntity.ok(
            flow {
                bookService.search(bookTitle, authorName, isbn).map {
                    emit(
                        Book(
                            id = it.id,
                            isbn = it.isbn,
                            title = it.title,
                            author =
                                BookAuthor(
                                    authorId = it.author.authorId,
                                    name = it.author.name,
                                    birthday = it.author.birthday,
                                ),
                        ),
                    )
                }
            },
        )
}
