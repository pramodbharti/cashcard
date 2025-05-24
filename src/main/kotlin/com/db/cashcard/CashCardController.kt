package com.db.cashcard

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/cashcards")
class CashCardController(private val cashCardRepository: CashCardRepository) {

    @GetMapping("/{requestId}")
    fun findById(@PathVariable requestId: Long): ResponseEntity<CashCard> {
        val cashCard = cashCardRepository.findById(requestId)
        return if (cashCard.isPresent) {
            ResponseEntity.ok(cashCard.get())
        } else {
            ResponseEntity.notFound().build()
        }
//        if (requestId == 99L) {
//            val cashCard = CashCard(99L, 123.45)
//            return ResponseEntity.ok(cashCard)
//        } else {
//            return ResponseEntity.notFound().build()
//        }
    }

    @PostMapping
    fun createCashCard(@RequestBody card: CashCard, ucb: UriComponentsBuilder): ResponseEntity<Void>? {
        val savedCashCard = cashCardRepository.save(card)
        val locationOfNewCashCard = ucb.path("cashcards/{id}").buildAndExpand(savedCashCard.id).toUri()
        return ResponseEntity.created(locationOfNewCashCard).build()
    }

//    @GetMapping
//    fun findAll(): ResponseEntity<Iterable<CashCard>> {
//        return ResponseEntity.ok(cashCardRepository.findAll())
//    }

    @GetMapping
    fun findAll(pageable: Pageable): ResponseEntity<Iterable<CashCard>> {
        val page: Page<CashCard> = cashCardRepository.findAll(
            PageRequest.of(
                pageable.pageNumber,
                pageable.pageSize,
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
            )
        )
        return ResponseEntity.ok(page.content)
    }
}