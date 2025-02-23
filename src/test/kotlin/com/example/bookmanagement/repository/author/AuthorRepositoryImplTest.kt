package com.example.bookmanagement.repository.author

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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest
class AuthorRepositoryImplTest
    @Autowired
    constructor(
        private val create: DSLContext,
        private var authorRepository: AuthorRepository,
    ) : RepositoryTest() {
        @AfterEach
        fun afterEach() =
            runTest {
                create.deleteFrom(BOOK).awaitFirstOrNull()
                create.deleteFrom(AUTHOR).awaitFirstOrNull()
            }

        @Test
        fun testCreateAuthor() =
            runTest {
                val id = authorRepository.create("岡本　太郎", LocalDate.of(2000, 1, 31))

                val actual =
                    create
                        .selectFrom(AUTHOR)
                        .where(AUTHOR.ID.eq(id))
                        .awaitFirstOrNull()

                assertNotNull(actual)
                assertEquals("岡本　太郎", actual.name)
                assertEquals(LocalDate.of(2000, 1, 31), actual.birthday)
            }

        @Test
        fun testUpdateAuthor() =
            runTest {
                val authorId =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME, AUTHOR.BIRTHDAY)
                        .values("山田　太郎", LocalDate.of(2023, 5, 13))
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                val updateCount = authorRepository.update(authorId, "岡本　太郎", LocalDate.of(2000, 1, 31))

                assertEquals(1, updateCount)

                val actual =
                    create
                        .selectFrom(AUTHOR)
                        .where(AUTHOR.ID.eq(authorId))
                        .awaitFirstOrNull()

                assertNotNull(actual)
                assertEquals("岡本　太郎", actual.name)
                assertEquals(LocalDate.of(2000, 1, 31), actual.birthday)
            }

        @Test
        fun testFindAuthorNotFound() =
            runTest {
                val actual = authorRepository.findById(0)

                assertNull(actual)
            }

        @Test
        fun testFindAuthorNoBook() =
            runTest {
                val authorId =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME, AUTHOR.BIRTHDAY)
                        .values("山田　太郎", LocalDate.of(2023, 5, 13))
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                val actual = authorRepository.findById(authorId)
                assertNotNull(actual)
                assertEquals("山田　太郎", actual.name)
                assertEquals(LocalDate.of(2023, 5, 13), actual.birthday)
                assertTrue(actual.books.isEmpty())
            }

        @Test
        fun testForOnlyRequiredAuthorItems() =
            runTest {
                val authorId =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME)
                        .values("山田　太郎")
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                val actual = authorRepository.findById(authorId)
                assertNotNull(actual)
                assertEquals("山田　太郎", actual.name)
                assertNull(actual.birthday)
            }

        @Test
        fun testGetAuthorAndBook() =
            runTest {
                val authorId =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME, AUTHOR.BIRTHDAY)
                        .values("夏目　漱石", LocalDate.of(2023, 5, 13))
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                create
                    .insertInto(BOOK)
                    .columns(BOOK.TITLE, BOOK.ISBN, BOOK.AUTHOR_ID)
                    .values("こころ", "9780720612974", authorId)
                    .awaitFirstOrNull()

                create
                    .insertInto(BOOK)
                    .columns(BOOK.TITLE, BOOK.AUTHOR_ID)
                    .values("坊ちゃん", authorId)
                    .awaitFirstOrNull()

                val actual = authorRepository.findById(authorId)
                assertNotNull(actual)
                assertEquals("夏目　漱石", actual.name)
                assertEquals(LocalDate.of(2023, 5, 13), actual.birthday)
                assertEquals(2, actual.books.size)
                val resultBook1 = actual.books[0]
                val resultBook2 = actual.books[1]
                assertEquals("こころ", resultBook1.title)
                assertEquals("9780720612974", resultBook1.isbn)
                assertEquals("坊ちゃん", resultBook2.title)
                assertNull(resultBook2.isbn)
            }

        @Test
        fun testExistsById() =
            runTest {
                val authorId =
                    create
                        .insertInto(AUTHOR)
                        .columns(AUTHOR.NAME, AUTHOR.BIRTHDAY)
                        .values("夏目　漱石", LocalDate.of(2023, 5, 13))
                        .returningResult(AUTHOR.ID)
                        .awaitSingle()
                        .map { it[AUTHOR.ID] }

                assertTrue(authorRepository.existsById(authorId))
            }

        @Test
        fun testExistsByIdNotExists() =
            runTest {
                assertFalse(authorRepository.existsById(0))
            }
    }
