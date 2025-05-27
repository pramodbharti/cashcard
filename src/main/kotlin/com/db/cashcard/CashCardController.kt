package com.db.cashcard

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.security.Principal


@RestController
@RequestMapping("/cashcards")
class CashCardController(private val cashCardRepository: CashCardRepository) {

    @GetMapping("/{requestId}")
    fun findById(@PathVariable requestId: Long, principal: Principal): ResponseEntity<CashCard> {
        val cashCard = cashCardRepository.findByIdAndOwner(requestId, principal.name)
        return if (cashCard.isPresent) {
            ResponseEntity.ok(cashCard.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createCashCard(
        @RequestBody card: CashCard,
        ucb: UriComponentsBuilder,
        principal: Principal
    ): ResponseEntity<Unit> {
        val cashCardWithOwner = CashCard(null, card.amount, principal.name)
        val savedCashCard = cashCardRepository.save(cashCardWithOwner)
        val locationOfNewCashCard = ucb.path("cashcards/{id}").buildAndExpand(savedCashCard.id).toUri()
        return ResponseEntity.created(locationOfNewCashCard).build()
    }

    @GetMapping
    fun findAll(pageable: Pageable, principal: Principal): ResponseEntity<List<CashCard>> {
        val page: Page<CashCard> = cashCardRepository.findByOwner(
            principal.name,
            PageRequest.of(
                pageable.pageNumber,
                pageable.pageSize,
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
            )
        )
        return ResponseEntity.ok(page.content)
    }

}