package com.example.bookmanagement.service.book

import com.example.bookmanagement.model.Book
import com.example.bookmanagement.repository.author.AuthorRepository
import com.example.bookmanagement.repository.book.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 書籍サービス実装
 */
@Service
class BookServiceImpl(private val bookRepository: BookRepository, private val authorRepository: AuthorRepository) : BookService {
    @Transactional
    override fun create(
        isbn: String?,
        authorId: Int,
        title: String,
    ): Int? {
        if (!authorRepository.existsById(authorId)) {
            return null
        }
        return bookRepository.create(isbn, authorId, title)
    }

    @Transactional
    override fun update(
        id: Int,
        isbn: String?,
        authorId: Int,
        title: String,
    ): Int? {
        if (!authorRepository.existsById(authorId)) {
            return null
        }
        if (bookRepository.update(id, isbn, authorId, title) == 0) {
            return null
        }
        return id
    }

    override fun search(
        title: String?,
        authorName: String?,
        isbn: String?,
    ): List<Book> {
        return bookRepository.search(title, authorName, isbn)
    }
}
