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
    suspend fun create(
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
    suspend fun update(
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
    suspend fun findById(id: Int): Author?
}
