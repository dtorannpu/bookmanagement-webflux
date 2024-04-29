package com.example.bookmanagement.repository.book

import com.example.bookmanagement.model.Book

/**
 * 書籍リポジトリ
 */
interface BookRepository {
    /**
     * 作成
     *
     * @param isbn ISBN
     * @param authorId 著者ID
     * @param title タイトル
     *
     * @return ID
     */
    fun create(
        isbn: String?,
        authorId: Int,
        title: String,
    ): Int

    /**
     * 更新
     *
     * @param id ID
     * @param isbn ISBN
     * @param authorId 著者ID
     * @param title タイトル
     *
     * @return 更新件数
     */
    fun update(
        id: Int,
        isbn: String?,
        authorId: Int,
        title: String,
    ): Int

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
