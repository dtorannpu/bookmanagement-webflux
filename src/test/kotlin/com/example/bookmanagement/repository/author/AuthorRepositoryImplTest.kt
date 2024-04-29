package com.example.bookmanagement.repository.author

import com.example.bookmanagement.db.jooq.gen.tables.references.AUTHOR
import com.example.bookmanagement.db.jooq.gen.tables.references.BOOK
import com.example.bookmanagement.repository.RepositoryTest
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@JooqTest
@Transactional
class AuthorRepositoryImplTest
    @Autowired
    constructor(
        private val create: DSLContext,
    ) : RepositoryTest() {
        private lateinit var authorRepository: AuthorRepository

        @BeforeEach
        fun setUp() {
            authorRepository = AuthorRepositoryImpl(create)
        }

        @Test
        fun testCreateAuthor() {
            val id = authorRepository.create("岡本　太郎", LocalDate.of(2000, 1, 31))

            val actual = create.fetchOne(AUTHOR, AUTHOR.ID.eq(id))
            assertNotNull(actual)
            assertEquals("岡本　太郎", actual.name)
            assertEquals(LocalDate.of(2000, 1, 31), actual.birthday)
        }

        @Test
        fun testUpdateAuthor() {
            val author = create.newRecord(AUTHOR)
            author.name = "山田　太郎"
            author.birthday = LocalDate.of(2023, 5, 13)
            author.store()

            val updateCount = authorRepository.update(author.id!!, "岡本　太郎", LocalDate.of(2000, 1, 31))

            assertEquals(1, updateCount)

            val actual = create.fetchOne(AUTHOR, AUTHOR.ID.eq(author.id))
            assertNotNull(actual)
            assertEquals("岡本　太郎", actual.name)
            assertEquals(LocalDate.of(2000, 1, 31), actual.birthday)
        }

        @Test
        fun testFindAuthorNotFound() {
            val actual = authorRepository.findById(0)

            assertNull(actual)
        }

        @Test
        fun testFindAuthorNoBook() {
            val author = create.newRecord(AUTHOR)
            author.name = "山田　太郎"
            author.birthday = LocalDate.of(2023, 5, 13)
            author.store()

            val actual = authorRepository.findById(author.id!!)
            assertNotNull(actual)
            assertEquals("山田　太郎", actual.name)
            assertEquals(LocalDate.of(2023, 5, 13), actual.birthday)
            assertTrue(actual.books.isEmpty())
        }

        @Test
        fun testForOnlyRequiredAuthorItems() {
            val author = create.newRecord(AUTHOR)
            author.name = "山田　太郎"
            author.store()

            val actual = authorRepository.findById(author.id!!)
            assertNotNull(actual)
            assertEquals("山田　太郎", actual.name)
            assertNull(actual.birthday)
        }

        @Test
        fun testGetAuthorAndBook() {
            val author = create.newRecord(AUTHOR)
            author.name = "夏目　漱石"
            author.birthday = LocalDate.of(2023, 5, 13)
            author.store()
            val authorId = author.id!!

            val book1 = create.newRecord(BOOK)
            book1.title = "こころ"
            book1.isbn = "9780720612974"
            book1.authorId = authorId
            book1.store()

            val book2 = create.newRecord(BOOK)
            book2.title = "坊ちゃん"
            book2.authorId = authorId
            book2.store()

            val actual = authorRepository.findById(author.id!!)
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
        fun testExistsById() {
            val author = create.newRecord(AUTHOR)
            author.name = "夏目　漱石"
            author.birthday = LocalDate.of(2023, 5, 13)
            author.store()
            val authorId = author.id!!

            assertTrue(authorRepository.existsById(authorId))
        }

        @Test
        fun testExistsByIdNotExists() {
            assertFalse(authorRepository.existsById(0))
        }
    }
