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
class CashCardController(private val cashCardService: CashCardService) {

    @GetMapping("/{requestId}")
    fun findById(@PathVariable requestId: Long, principal: Principal): ResponseEntity<CashCard> {
        val cashCard = findCashCard(requestId, principal)
        return if (cashCard.isPresent) {
            ResponseEntity.ok(cashCard.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{requestedId}")
    fun deleteCashCard(@PathVariable requestedId: Long, principal: Principal): ResponseEntity<Unit> {
        return if (cashCardService.existsByIdAndOwner(requestedId, principal.name)) {
            cashCardService.deleteById(requestedId)
            ResponseEntity.noContent().build()
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
        val savedCashCard = cashCardService.save(cashCardWithOwner)
        val locationOfNewCashCard = ucb.path("cashcards/{id}").buildAndExpand(savedCashCard.id).toUri()
        return ResponseEntity.created(locationOfNewCashCard).build()
    }

    @GetMapping
    fun findAll(pageable: Pageable, principal: Principal): ResponseEntity<List<CashCard>> {
        val page: Page<CashCard> = cashCardService.findByOwner(
            principal.name,
            PageRequest.of(
                pageable.pageNumber,
                pageable.pageSize,
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
            )
        )
        return ResponseEntity.ok(page.content)
    }

    @PutMapping("/{requestedId}")
    fun updateCashCard(
        @PathVariable requestedId: Long,
        @RequestBody card: CashCard,
        principal: Principal
    ): ResponseEntity<Unit> {
        val cashCard = findCashCard(requestedId, principal)
        return if (cashCard.isPresent) {
            val updatedCard = CashCard(cashCard.get().id, card.amount, principal.name)
            cashCardService.save(updatedCard)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    private fun findCashCard(requestedId: Long, principal: Principal) =
        cashCardService.findByIdAndOwner(requestedId, principal.name)

}
