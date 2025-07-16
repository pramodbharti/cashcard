package com.db.cashcard

import org.springframework.data.annotation.Id

class CashCard(
    @Id
    val id: Long?,
    val amount: Double,
    val owner: String?
)
