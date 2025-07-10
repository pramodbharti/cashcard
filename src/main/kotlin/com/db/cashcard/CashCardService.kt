package com.db.cashcard

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.util.Optional

/**
 * Service interface for managing cash cards.
 */
interface CashCardService {
    fun findByIdAndOwner(id: Long, owner: String): Optional<CashCard>
    fun findByOwner(owner: String, pageRequest: PageRequest): Page<CashCard>
    fun existsByIdAndOwner(id: Long, owner: String): Boolean
    fun save(cashCard: CashCard): CashCard
    fun deleteById(id: Long)
}