package com.example.bookmanagement.model

/**
 * 書籍
 *
 * @param id ID
 * @param isbn ISBN
 * @param title タイトル
 * @param author 書籍の著者
 */
data class Book(val id: Int, val isbn: String?, val title: String, val author: BookAuthor)
