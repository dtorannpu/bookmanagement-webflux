package com.example.bookmanagement.service.book

import com.example.bookmanagement.TransactionCoroutineOperator
import com.example.bookmanagement.model.Book
import com.example.bookmanagement.repository.author.AuthorRepository
import com.example.bookmanagement.repository.book.BookRepository
import org.springframework.stereotype.Service

/**
 * 書籍サービス実装
 */
@Service
class BookServiceImpl(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val transactionCoroutineOperator: TransactionCoroutineOperator,
) : BookService {
    override suspend fun create(
        isbn: String?,
        authorId: Int,
        title: String,
    ): Int? {
        return transactionCoroutineOperator.execute {
            if (!authorRepository.existsById(authorId)) {
                return@execute null
            }
            return@execute bookRepository.create(isbn, authorId, title)
        }
    }

    override suspend fun update(
        id: Int,
        isbn: String?,
        authorId: Int,
        title: String,
    ): Int? {
        return transactionCoroutineOperator.execute {
            if (!authorRepository.existsById(authorId)) {
                return@execute null
            }
            if (bookRepository.update(id, isbn, authorId, title) == 0) {
                return@execute null
            }
            return@execute id
        }
    }

    override suspend fun search(
        title: String?,
        authorName: String?,
        isbn: String?,
    ): List<Book> {
        return bookRepository.search(title, authorName, isbn)
    }
}
