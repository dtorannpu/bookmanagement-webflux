package com.example.bookmanagement.repository.book

import com.example.bookmanagement.TransactionAwareDSLContext
import com.example.bookmanagement.db.jooq.gen.tables.references.BOOK
import com.example.bookmanagement.model.Book
import com.example.bookmanagement.model.BookAuthor
import kotlinx.coroutines.reactive.awaitSingle
import org.jooq.Operator
import org.jooq.impl.DSL.row
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

/**
 * 書籍リポジトリ実装
 */
@Repository
class BookRepositoryImpl(
    private val dslContext: TransactionAwareDSLContext,
) : BookRepository {
    override suspend fun create(
        isbn: String?,
        authorId: Int,
        title: String,
    ): Int =
        dslContext
            .get()
            .insertInto(BOOK)
            .columns(BOOK.ISBN, BOOK.AUTHOR_ID, BOOK.TITLE)
            .values(isbn, authorId, title)
            .returningResult(BOOK.ID)
            .awaitSingle()
            .map { it[BOOK.ID] }

    override suspend fun update(
        id: Int,
        isbn: String?,
        authorId: Int,
        title: String,
    ): Int =
        dslContext
            .get()
            .update(BOOK)
            .set(BOOK.ISBN, isbn)
            .set(BOOK.AUTHOR_ID, authorId)
            .set(BOOK.TITLE, title)
            .where(BOOK.ID.eq(id))
            .awaitSingle()

    override suspend fun search(
        title: String?,
        authorName: String?,
        isbn: String?,
    ): List<Book> {
        val query =
            dslContext
                .get()
                .select(
                    BOOK.ID,
                    BOOK.ISBN,
                    BOOK.TITLE,
                    row(
                        BOOK.author().ID,
                        BOOK.author().NAME,
                        BOOK.author().BIRTHDAY,
                    ).mapping { id, name, birthday -> BookAuthor(authorId = id!!, name = name!!, birthday = birthday) },
                ).from(BOOK)
                .query

        if (title != null) {
            query.addConditions(Operator.AND, BOOK.TITLE.like("%$title%"))
        }

        if (isbn != null) {
            query.addConditions(Operator.AND, BOOK.ISBN.eq(isbn))
        }

        if (authorName != null) {
            query.addConditions(Operator.AND, BOOK.author().NAME.like("%$authorName%"))
        }

        return Flux
            .from(query)
            .map {
                Book(
                    id = it[BOOK.ID]!!,
                    isbn = it[BOOK.ISBN],
                    title = it[BOOK.TITLE]!!,
                    author = it.value4(),
                )
            }.collectList()
            .awaitSingle()
    }
}
