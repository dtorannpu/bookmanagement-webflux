package com.example.bookmanagement.repository.book

import com.example.bookmanagement.db.jooq.gen.tables.references.BOOK
import com.example.bookmanagement.model.Book
import com.example.bookmanagement.model.BookAuthor
import org.jooq.DSLContext
import org.jooq.Operator
import org.jooq.impl.DSL.row
import org.springframework.stereotype.Repository

/**
 * 書籍リポジトリ実装
 */
@Repository
class BookRepositoryImpl(private val create: DSLContext) : BookRepository {
    override fun create(
        isbn: String?,
        authorId: Int,
        title: String,
    ): Int {
        val book = create.newRecord(BOOK)
        book.isbn = isbn
        book.authorId = authorId
        book.title = title
        book.store()

        return book.id!!
    }

    override fun update(
        id: Int,
        isbn: String?,
        authorId: Int,
        title: String,
    ): Int {
        return create.update(BOOK)
            .set(BOOK.ISBN, isbn)
            .set(BOOK.AUTHOR_ID, authorId)
            .set(BOOK.TITLE, title)
            .where(BOOK.ID.eq(id))
            .execute()
    }

    override fun search(
        title: String?,
        authorName: String?,
        isbn: String?,
    ): List<Book> {
        val query =
            create.select(
                BOOK.ID,
                BOOK.ISBN,
                BOOK.TITLE,
                row(
                    BOOK.author().ID,
                    BOOK.author().NAME,
                    BOOK.author().BIRTHDAY,
                ).mapping { id, name, birthday -> BookAuthor(authorId = id!!, name = name!!, birthday = birthday) },
            )
                .from(BOOK).query

        if (title != null) {
            query.addConditions(Operator.AND, BOOK.TITLE.like("%$title%"))
        }

        if (isbn != null) {
            query.addConditions(Operator.AND, BOOK.ISBN.eq(isbn))
        }

        if (authorName != null) {
            query.addConditions(Operator.AND, BOOK.author().NAME.like("%$authorName%"))
        }

        return query.fetch {
            Book(
                id = it[BOOK.ID]!!,
                isbn = it[BOOK.ISBN],
                title = it[BOOK.TITLE]!!,
                author = it.value4(),
            )
        }
    }
}
