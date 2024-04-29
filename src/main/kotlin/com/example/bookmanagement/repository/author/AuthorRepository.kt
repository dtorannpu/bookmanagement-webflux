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
    fun create(
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
    fun update(
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
    fun findById(id: Int): Author?

    /**
     * 著者IDの存在チェック
     *
     * @param id ID
     */
    fun existsById(id: Int): Boolean
}
