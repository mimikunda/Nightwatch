package com.mimikunda.nightwatch.domain.model

data class SeatPosition(
    val edge: TableEdge,
    val positionOnEdge: Int,
    val normalizedOffset: Float = 0f,
    val customNormalizedX: Float? = null,
    val customNormalizedY: Float? = null
)
