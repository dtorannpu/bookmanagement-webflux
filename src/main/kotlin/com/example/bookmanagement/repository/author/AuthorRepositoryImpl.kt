package com.example.bookmanagement.repository.author

import com.example.bookmanagement.TransactionAwareDSLContext
import com.example.bookmanagement.db.jooq.gen.tables.references.AUTHOR
import com.example.bookmanagement.model.Author
import com.example.bookmanagement.model.AuthorBook
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.select
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * 著者リポジトリ実装
 */
@Repository
class AuthorRepositoryImpl(private val dslContext: TransactionAwareDSLContext) : AuthorRepository {
    override suspend fun create(
        name: String,
        birthday: LocalDate?,
    ): Int {
        return dslContext.get()
            .insertInto(AUTHOR)
            .columns(AUTHOR.NAME, AUTHOR.BIRTHDAY)
            .values(name, birthday)
            .returningResult(AUTHOR.ID)
            .awaitSingle().map { it[AUTHOR.ID] }
    }

    override suspend fun update(
        id: Int,
        name: String,
        birthday: LocalDate?,
    ): Int {
        return dslContext.get().update(AUTHOR)
            .set(AUTHOR.NAME, name)
            .set(AUTHOR.BIRTHDAY, birthday)
            .where(AUTHOR.ID.eq(id))
            .awaitSingle()
    }

    override suspend fun findById(id: Int): Author? {
        return dslContext.get().select(
            AUTHOR.ID,
            AUTHOR.NAME,
            AUTHOR.BIRTHDAY,
            multiset(
                select(AUTHOR.book().ID, AUTHOR.book().ISBN, AUTHOR.book().TITLE)
                    .from(AUTHOR.book()),
            )
                .convertFrom { r ->
                    r.map {
                        AuthorBook(
                            id = it[AUTHOR.book().ID]!!,
                            isbn = it[AUTHOR.book().ISBN],
                            title = it[AUTHOR.book().TITLE]!!,
                        )
                    }
                },
        ).from(AUTHOR)
            .where(AUTHOR.ID.eq(id))
            .awaitFirstOrNull()?.let {
                Author(
                    id = it[AUTHOR.ID]!!,
                    name = it[AUTHOR.NAME]!!,
                    birthday = it[AUTHOR.BIRTHDAY],
                    books = it.value4(),
                )
            }
    }

    override suspend fun existsById(id: Int): Boolean {
        val count =
            dslContext.get().selectCount()
                .from(AUTHOR)
                .where(AUTHOR.ID.eq(id))
                .awaitSingle()[0]
        return count == 1
    }
}
