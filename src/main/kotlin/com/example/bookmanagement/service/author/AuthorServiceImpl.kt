package com.example.bookmanagement.service.author

import com.example.bookmanagement.model.Author
import com.example.bookmanagement.repository.author.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * 著者サービス実装
 */
@Service
class AuthorServiceImpl(private val authorRepository: AuthorRepository) :
    AuthorService {
    @Transactional
    override fun create(
        name: String,
        birthday: LocalDate?,
    ): Int {
        return authorRepository.create(name, birthday)
    }

    @Transactional
    override fun update(
        id: Int,
        name: String,
        birthday: LocalDate?,
    ): Int? {
        if (authorRepository.update(id, name, birthday) == 0) {
            return null
        }
        return id
    }

    override fun findById(id: Int): Author? {
        return authorRepository.findById(id)
    }
}
