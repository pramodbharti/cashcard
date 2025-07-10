package com.db.cashcard

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.Optional

/**
 * Implementation of the CashCardService interface.
 */
@Service
class CashCardServiceImpl(private val cashCardRepository: CashCardRepository) : CashCardService {

    override fun findByIdAndOwner(id: Long, owner: String): Optional<CashCard> {
        return cashCardRepository.findByIdAndOwner(id, owner)
    }

    override fun findByOwner(owner: String, pageRequest: PageRequest): Page<CashCard> {
        return cashCardRepository.findByOwner(owner, pageRequest)
    }

    override fun existsByIdAndOwner(id: Long, owner: String): Boolean {
        return cashCardRepository.existsByIdAndOwner(id, owner)
    }

    override fun save(cashCard: CashCard): CashCard {
        return cashCardRepository.save(cashCard)
    }

    override fun deleteById(id: Long) {
        cashCardRepository.deleteById(id)
    }
}