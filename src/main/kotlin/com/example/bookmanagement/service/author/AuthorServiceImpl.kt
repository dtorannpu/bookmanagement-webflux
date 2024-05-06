package com.example.bookmanagement.service.author

import com.example.bookmanagement.TransactionCoroutineOperator
import com.example.bookmanagement.model.Author
import com.example.bookmanagement.repository.author.AuthorRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * 著者サービス実装
 */
@Service
class AuthorServiceImpl(
    private val authorRepository: AuthorRepository,
    private val transactionCoroutineOperator: TransactionCoroutineOperator,
) :
    AuthorService {
    override suspend fun create(
        name: String,
        birthday: LocalDate?,
    ): Int {
        return transactionCoroutineOperator.execute { authorRepository.create(name, birthday) }
    }

    override suspend fun update(
        id: Int,
        name: String,
        birthday: LocalDate?,
    ): Int? {
        return transactionCoroutineOperator.execute {
            if (authorRepository.update(id, name, birthday) == 0) {
                return@execute null
            }
            return@execute id
        }
    }

    override suspend fun findById(id: Int): Author? {
        return authorRepository.findById(id)
    }
}
