package com.example.bookmanagement.service.book

import com.example.bookmanagement.model.Book

/**
 * 書籍サービス
 */
interface BookService {
    /**
     * 作成
     *
     * @param isbn ISBN
     * @param authorId 著者ID
     * @param title タイトル
     *
     * @return 登録に成功した場合ID
     */
    fun create(
        isbn: String?,
        authorId: Int,
        title: String,
    ): Int?

    /**
     * 更新
     *
     * @param id ID
     * @param isbn ISBN
     * @param authorId 著者ID
     * @param title タイトル
     *
     * @return 更新に成功した場合ID
     */
    fun update(
        id: Int,
        isbn: String?,
        authorId: Int,
        title: String,
    ): Int?

    /**
     * 検索
     *
     * @param title タイトル
     * @param authorName 著者名
     * @param isbn ISBN
     *
     * @return 書籍リスト
     */
    fun search(
        title: String?,
        authorName: String?,
        isbn: String?,
    ): List<Book>
}
