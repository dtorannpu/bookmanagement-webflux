package com.example.bookmanagement.model

import java.time.LocalDate

/**
 * 書籍の著者
 *
 * @param authorId ID
 * @param name 著者名
 * @param birthday 誕生日
 */
data class BookAuthor(
    val authorId: Int,
    val name: String,
    val birthday: LocalDate?,
)
