package com.example.bookmanagement.controller.author

import com.example.bookmanagement.api.AuthorsApi
import com.example.bookmanagement.service.author.AuthorService
import org.springframework.web.bind.annotation.RestController

/**
 * 著者コントローラー
 */
@RestController
class AuthorApiController(private val authorService: AuthorService) : AuthorsApi
