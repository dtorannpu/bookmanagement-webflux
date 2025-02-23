package com.example.bookmanagement.repository.book

import com.example.bookmanagement.db.jooq.gen.tables.references.AUTHOR
import com.example.bookmanagement.db.jooq.gen.tables.references.BOOK
import com.example.bookmanagement.repository.RepositoryTest
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
class BookRepositoryImplTest
    @Autowired
    constructor(
        private val create: DSLContext,
        private val bookRepository: BookRepositoryImpl,
    ) : RepositoryTest() {
        @AfterEach
        fun afterEach() =
            runTest {
                create.deleteFrom(BOOK).awaitFirstOrNull()
                create.deleteFrom(AUTHOR).awaitFirstOrNull()
            }

        @Test
        fun testCreateBook() =
            runTest {
                val authorId =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME, AUTHOR.BIRTHDAY)
                        .values("山田　太郎", LocalDate.of(2023, 5, 13))
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                val bookId = bookRepository.create("1234567890", authorId, "こころ")

                val actual =
                    create
                        .selectFrom(BOOK)
                        .where(BOOK.ID.eq(bookId))
                        .awaitFirstOrNull()

                assertNotNull(actual)
                assertEquals(authorId, actual.authorId)
                assertEquals("1234567890", actual.isbn)
                assertEquals("こころ", actual.title)
            }

        @Test
        fun testUpdateBook() =
            runTest {
                val changeBeforeAuthorAuthorId =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME, AUTHOR.BIRTHDAY)
                        .values("山田　太郎", LocalDate.of(2023, 5, 13))
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                val changeAfterAuthorAuthorId =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME, AUTHOR.BIRTHDAY)
                        .values("山田　次郎", LocalDate.of(2000, 1, 1))
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                val bookId =
                    create
                        .insertInto(BOOK)
                        .columns(BOOK.ISBN, BOOK.TITLE, BOOK.AUTHOR_ID)
                        .values("1234567890", "こころ", changeBeforeAuthorAuthorId)
                        .returningResult(BOOK.ID)
                        .awaitSingle()
                        .map { it[BOOK.ID] }

                val updateCount = bookRepository.update(bookId, "0123456789", changeAfterAuthorAuthorId, "坊ちゃん")

                assertEquals(1, updateCount)

                val actual =
                    create
                        .selectFrom(BOOK)
                        .where(BOOK.ID.eq(bookId))
                        .awaitFirstOrNull()

                assertNotNull(actual)
                assertEquals(changeAfterAuthorAuthorId, actual.authorId)
                assertEquals("0123456789", actual.isbn)
                assertEquals("坊ちゃん", actual.title)
            }

        @Test
        fun testBookSearchNoCondition() =
            runTest {
                val author1Id =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME, AUTHOR.BIRTHDAY)
                        .values("山田　太郎", LocalDate.of(2023, 5, 13))
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                val book1Id =
                    create
                        .insertInto(BOOK)
                        .columns(BOOK.ISBN, BOOK.TITLE, BOOK.AUTHOR_ID)
                        .values("1234567890", "こころ", author1Id)
                        .returningResult(BOOK.ID)
                        .awaitSingle()
                        .map { it[BOOK.ID] }

                val author2Id =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME)
                        .values("山田　次郎")
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                val book2Id =
                    create
                        .insertInto(BOOK)
                        .columns(BOOK.TITLE, BOOK.AUTHOR_ID)
                        .values("坊ちゃん", author2Id)
                        .returningResult(BOOK.ID)
                        .awaitSingle()
                        .map { it[BOOK.ID] }

                val actual = bookRepository.search(null, null, null)

                assertEquals(2, actual.size)
                val resultBook1 = actual[0]
                val resultBook2 = actual[1]

                assertEquals(book1Id, resultBook1.id)
                assertEquals("1234567890", resultBook1.isbn)
                assertEquals("こころ", resultBook1.title)

                assertEquals(book2Id, resultBook2.id)
                assertNull(resultBook2.isbn)
                assertEquals("坊ちゃん", resultBook2.title)
            }

        @Test
        fun testBookSearchByTitle() =
            runTest {
                val author1Id =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME, AUTHOR.BIRTHDAY)
                        .values("山田　太郎", LocalDate.of(2023, 5, 13))
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                val book1Id =
                    create
                        .insertInto(BOOK)
                        .columns(BOOK.ISBN, BOOK.TITLE, BOOK.AUTHOR_ID)
                        .values("1234567890", "こころ", author1Id)
                        .returningResult(BOOK.ID)
                        .awaitSingle()
                        .map { it[BOOK.ID] }

                val author2Id =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME)
                        .values("山田　次郎")
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                create
                    .insertInto(BOOK)
                    .columns(BOOK.TITLE, BOOK.AUTHOR_ID)
                    .values("坊ちゃん", author2Id)
                    .awaitFirstOrNull()

                val actual = bookRepository.search("こころ", null, null)

                assertEquals(1, actual.size)
                val resultBook1 = actual[0]

                assertEquals(book1Id, resultBook1.id)
                assertEquals("1234567890", resultBook1.isbn)
                assertEquals("こころ", resultBook1.title)
            }

        @Test
        fun testBookSearchByIsbn() =
            runTest {
                val author1Id =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME, AUTHOR.BIRTHDAY)
                        .values("山田　太郎", LocalDate.of(2023, 5, 13))
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                val book1Id =
                    create
                        .insertInto(BOOK)
                        .columns(BOOK.ISBN, BOOK.TITLE, BOOK.AUTHOR_ID)
                        .values("1234567890", "こころ", author1Id)
                        .returningResult(BOOK.ID)
                        .awaitSingle()
                        .map { it[BOOK.ID] }

                val author2Id =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME)
                        .values("山田　次郎")
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                create
                    .insertInto(BOOK)
                    .columns(BOOK.TITLE, BOOK.AUTHOR_ID)
                    .values("坊ちゃん", author2Id)
                    .returningResult(BOOK.ID)
                    .awaitFirstOrNull()

                val actual = bookRepository.search(null, null, "1234567890")

                assertEquals(1, actual.size)
                val resultBook1 = actual[0]

                assertEquals(book1Id, resultBook1.id)
                assertEquals("1234567890", resultBook1.isbn)
                assertEquals("こころ", resultBook1.title)
            }

        @Test
        fun testBookSearchByAuthorName() =
            runTest {
                val author1Id =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME, AUTHOR.BIRTHDAY)
                        .values("山田　太郎", LocalDate.of(2023, 5, 13))
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                create
                    .insertInto(BOOK)
                    .columns(BOOK.ISBN, BOOK.TITLE, BOOK.AUTHOR_ID)
                    .values("1234567890", "こころ", author1Id)
                    .awaitFirstOrNull()

                val author2Id =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME)
                        .values("山田　次郎")
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                val book2Id =
                    create
                        .insertInto(BOOK)
                        .columns(BOOK.TITLE, BOOK.AUTHOR_ID)
                        .values("坊ちゃん", author2Id)
                        .returningResult(BOOK.ID)
                        .awaitSingle()
                        .map { it[BOOK.ID] }

                val actual = bookRepository.search(null, "次郎", null)

                assertEquals(1, actual.size)
                val resultBook1 = actual[0]

                assertEquals(book2Id, resultBook1.id)
                assertNull(resultBook1.isbn)
                assertEquals("坊ちゃん", resultBook1.title)
            }
    }
