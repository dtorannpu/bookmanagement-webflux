package com.example.bookmanagement.service.author

import com.example.bookmanagement.model.Author
import java.time.LocalDate

/**
 * 著者サービス
 */
interface AuthorService {
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
     * @param id ID
     * @param name 著者名
     * @param birthday 誕生日
     *
     * @return 更新に成功した場合ID
     */
    fun update(
        id: Int,
        name: String,
        birthday: LocalDate?,
    ): Int?

    /**
     * IDで著者を検索
     *
     * @param id ID
     * @return 著者
     */
    fun findById(id: Int): Author?
}
