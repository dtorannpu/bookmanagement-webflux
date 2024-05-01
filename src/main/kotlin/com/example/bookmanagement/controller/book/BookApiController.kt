package com.example.bookmanagement.controller.book

import com.example.bookmanagement.api.BooksApi
import com.example.bookmanagement.service.book.BookService
import org.springframework.web.bind.annotation.RestController

/**
 * 書籍コントローラー
 */
@RestController
class BookApiController(private val bookService: BookService) : BooksApi
