package com.example.bookmanagement.repository.book

import com.example.bookmanagement.db.jooq.gen.tables.references.AUTHOR
import com.example.bookmanagement.db.jooq.gen.tables.references.BOOK
import com.example.bookmanagement.repository.RepositoryTest
import kotlinx.coroutines.test.runTest
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

// @JooqTest
// @ActiveProfiles("test")
// @Import(TestDSLContextConnection::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookRepositoryImplTest : RepositoryTest() {
//        private lateinit var bookRepository: BookRepository
    @Autowired
    private lateinit var create: DSLContext

    @Autowired
    private lateinit var bookRepository: BookRepositoryImpl

    @BeforeEach
    fun setUp() {
//            val connectionFactory =
//                ConnectionFactories.get(
//                    ConnectionFactoryOptions.builder()
//                        .option(DRIVER, "postgresql")
//                        .option(HOST, db.host)
//                        .option(PORT, db.firstMappedPort)
//                        .option(USER, db.username)
//                        .option(PASSWORD, db.password)
//                        .option(DATABASE, db.databaseName)
//                        .build(),
//                )
//            bookRepository = BookRepositoryImpl(DSL.using(connectionFactory).dsl())
    }

    @Test
    fun testCreateBook() =
        runTest {
            val author = create.newRecord(AUTHOR)
            author.name = "山田　太郎"
            author.birthday = LocalDate.of(2023, 5, 13)
            author.store()

            val authorId = author.id!!

            val bookId = bookRepository.create("1234567890", authorId, "こころ")

            val actual = create.fetchOne(BOOK, BOOK.ID.eq(bookId))
            assertNotNull(actual)
            assertEquals(authorId, actual.authorId)
            assertEquals("1234567890", actual.isbn)
            assertEquals("こころ", actual.title)
        }

    @Test
    fun testUpdateBook() =
        runTest {
            val changeBeforeAuthor = create.newRecord(AUTHOR)
            changeBeforeAuthor.name = "山田　太郎"
            changeBeforeAuthor.birthday = LocalDate.of(2023, 5, 13)
            changeBeforeAuthor.store()
            val changeBeforeAuthorAuthorId = changeBeforeAuthor.id!!

            val changeAfterAuthor = create.newRecord(AUTHOR)
            changeAfterAuthor.name = "山田　次郎"
            changeAfterAuthor.birthday = LocalDate.of(2000, 1, 1)
            changeAfterAuthor.store()
            val changeAfterAuthorAuthorId = changeAfterAuthor.id!!

            val book = create.newRecord(BOOK)
            book.isbn = "1234567890"
            book.title = "こころ"
            book.authorId = changeBeforeAuthorAuthorId
            book.store()
            val bookId = book.id!!

            val updateCount = bookRepository.update(bookId, "0123456789", changeAfterAuthorAuthorId, "坊ちゃん")

            assertEquals(1, updateCount)

            val actual = create.fetchOne(BOOK, BOOK.ID.eq(bookId))
            assertNotNull(actual)
            assertEquals(changeAfterAuthorAuthorId, actual.authorId)
            assertEquals("0123456789", actual.isbn)
            assertEquals("坊ちゃん", actual.title)
        }

    @Test
    fun testBookSearchNoCondition() =
        runTest {
            val author1 = create.newRecord(AUTHOR)
            author1.name = "山田　太郎"
            author1.birthday = LocalDate.of(2023, 5, 13)
            author1.store()
            val author1Id = author1.id!!

            val book1 = create.newRecord(BOOK)
            book1.isbn = "1234567890"
            book1.title = "こころ"
            book1.authorId = author1Id
            book1.store()
            val book1Id = book1.id!!

            val author2 = create.newRecord(AUTHOR)
            author2.name = "山田　次郎"
            author2.store()
            val author2Id = author2.id!!

            val book2 = create.newRecord(BOOK)
            book2.title = "坊ちゃん"
            book2.authorId = author2Id
            book2.store()
            val book2Id = book2.id!!

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
            val author1 = create.newRecord(AUTHOR)
            author1.name = "山田　太郎"
            author1.birthday = LocalDate.of(2023, 5, 13)
            author1.store()
            val author1Id = author1.id!!

            val book1 = create.newRecord(BOOK)
            book1.isbn = "1234567890"
            book1.title = "こころ"
            book1.authorId = author1Id
            book1.store()
            val book1Id = book1.id!!

            val author2 = create.newRecord(AUTHOR)
            author2.name = "山田　次郎"
            author2.store()
            val author2Id = author2.id!!

            val book2 = create.newRecord(BOOK)
            book2.title = "坊ちゃん"
            book2.authorId = author2Id
            book2.store()

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
            val author1 = create.newRecord(AUTHOR)
            author1.name = "山田　太郎"
            author1.birthday = LocalDate.of(2023, 5, 13)
            author1.store()
            val author1Id = author1.id!!

            val book1 = create.newRecord(BOOK)
            book1.isbn = "1234567890"
            book1.title = "こころ"
            book1.authorId = author1Id
            book1.store()
            val book1Id = book1.id!!

            val author2 = create.newRecord(AUTHOR)
            author2.name = "山田　次郎"
            author2.store()
            val author2Id = author2.id!!

            val book2 = create.newRecord(BOOK)
            book2.title = "坊ちゃん"
            book2.authorId = author2Id
            book2.store()

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
            val author1 = create.newRecord(AUTHOR)
            author1.name = "山田　太郎"
            author1.birthday = LocalDate.of(2023, 5, 13)
            author1.store()
            val author1Id = author1.id!!

            val book1 = create.newRecord(BOOK)
            book1.isbn = "1234567890"
            book1.title = "こころ"
            book1.authorId = author1Id
            book1.store()

            val author2 = create.newRecord(AUTHOR)
            author2.name = "山田　次郎"
            author2.store()
            val author2Id = author2.id!!

            val book2 = create.newRecord(BOOK)
            book2.title = "坊ちゃん"
            book2.authorId = author2Id
            book2.store()
            val book2Id = book2.id!!

            val actual = bookRepository.search(null, "次郎", null)

            assertEquals(1, actual.size)
            val resultBook1 = actual[0]

            assertEquals(book2Id, resultBook1.id)
            assertNull(resultBook1.isbn)
            assertEquals("坊ちゃん", resultBook1.title)
        }
}
