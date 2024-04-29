package com.example.bookmanagement.controller.book

import com.example.bookmanagement.api.BooksApi
import com.example.bookmanagement.api.model.Book
import com.example.bookmanagement.api.model.BookAuthor
import com.example.bookmanagement.api.model.CreateBookRequest
import com.example.bookmanagement.api.model.CreateBookResponse
import com.example.bookmanagement.api.model.UpdateBookRequest
import com.example.bookmanagement.api.model.UpdateBookResponse
import com.example.bookmanagement.service.book.BookService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * 書籍コントローラー
 */
@RestController
class BookApiController(private val bookService: BookService) : BooksApi {
    override fun createBook(createBookRequest: CreateBookRequest): ResponseEntity<CreateBookResponse> {
        val id =
            bookService.create(createBookRequest.isbn, createBookRequest.authorId, createBookRequest.title)
                ?: return ResponseEntity(HttpStatus.OK)
        return ResponseEntity.ok(CreateBookResponse(id))
    }

    override fun updateBook(updateBookRequest: UpdateBookRequest): ResponseEntity<UpdateBookResponse> {
        val id =
            bookService.update(
                updateBookRequest.id,
                updateBookRequest.isbn,
                updateBookRequest.authorId,
                updateBookRequest.title,
            ) ?: return ResponseEntity(HttpStatus.OK)
        return ResponseEntity.ok(UpdateBookResponse(id))
    }

    override fun searchBook(
        bookTitle: String?,
        authorName: String?,
        isbn: String?,
    ): ResponseEntity<List<Book>> {
        val books = bookService.search(bookTitle, authorName, isbn)
        return ResponseEntity.ok(
            books.map {
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
                )
            },
        )
    }
}
