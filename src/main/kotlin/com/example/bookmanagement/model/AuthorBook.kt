package com.example.bookmanagement.model

/**
 * 著者の書籍
 *
 * @param id ID
 * @param isbn ISBN
 * @param title タイトル
 */
data class AuthorBook(
    val id: Int,
    val isbn: String?,
    val title: String,
)
