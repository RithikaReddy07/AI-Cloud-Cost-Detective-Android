package com.example.ui.components

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object CostFormatters {
    fun formatCurrency(amount: Double, currency: String = "USD"): String {
        val symbol = when (currency) {
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            else -> "$"
        }
        return if (currency == "JPY") {
            "$symbol${Math.round(amount * 150.0)}"
        } else {
            "$symbol${String.format("%,.2f", amount)}"
        }
    }

    fun formatDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("MMM dd"))
    }

    fun formatDateFull(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}
