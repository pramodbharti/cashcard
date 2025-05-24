package com.db.cashcard

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface CashCardRepository : CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long>