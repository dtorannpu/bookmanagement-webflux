package com.example.bookmanagement.repository.author

import com.example.bookmanagement.model.Author
import java.time.LocalDate

/**
 * 著者リポジトリ
 */
interface AuthorRepository {
    /**
     * 作成
     *
     * @param name 著者名
     * @param birthday 誕生日
     *
     * @return ID
     */
    suspend fun create(
        name: String,
        birthday: LocalDate?,
    ): Int

    /**
     * 更新
     *
     * @param name　著者名
     * @param birthday 誕生日
     *
     * @return 更新件数
     */
    suspend fun update(
        id: Int,
        name: String,
        birthday: LocalDate?,
    ): Int

    /**
     * IDで著者取得
     *
     * @param id ID
     * @return 著者
     */
    suspend fun findById(id: Int): Author?

    /**
     * 著者IDの存在チェック
     *
     * @param id ID
     */
    suspend fun existsById(id: Int): Boolean
}
