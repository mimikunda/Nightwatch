package com.mimikunda.nightwatch.domain.model

data class Seat(
    val id: String,
    val index: Int, // Ordered perimeter sequence index (0 .. N-1)
    val name: String? = null,
    val roleId: String? = null,
    val position: SeatPosition,
    val isAlive: Boolean = true,
    val activeStatuses: Set<StatusMarker> = emptySet(),
    val customStatusIds: Set<String> = emptySet(),
    val linkedSeatIds: Set<String> = emptySet()
)

/**
 * Resolves perimeter clockwise and counter-clockwise adjacent seats.
 * Returns Pair(leftNeighbor / CCW, rightNeighbor / CW).
 * Uses modular arithmetic: seat index 0's CCW neighbor is index N - 1, CW is index 1.
 */
fun Seat.neighbors(allSeats: List<Seat>): Pair<Seat, Seat> {
    require(allSeats.isNotEmpty()) { "Seat list cannot be empty" }

    val perimeterSeats = allSeats
        .filter { it.position.edge != TableEdge.FLOATING }
        .sortedBy { it.index }

    if (perimeterSeats.isEmpty() || this.position.edge == TableEdge.FLOATING) {
        val selfIndex = allSeats.indexOfFirst { it.id == this.id }
        if (selfIndex == -1 || allSeats.size == 1) return Pair(this, this)
        val total = allSeats.size
        val ccw = allSeats[(selfIndex - 1 + total) % total]
        val cw = allSeats[(selfIndex + 1) % total]
        return Pair(ccw, cw)
    }

    val totalPerimeter = perimeterSeats.size
    if (totalPerimeter == 1) return Pair(perimeterSeats[0], perimeterSeats[0])

    val currentPerimeterIndex = perimeterSeats.indexOfFirst { it.id == this.id }
    if (currentPerimeterIndex == -1) {
        val selfIndex = allSeats.indexOfFirst { it.id == this.id }
        val total = allSeats.size
        val ccw = allSeats[(selfIndex - 1 + total) % total]
        val cw = allSeats[(selfIndex + 1) % total]
        return Pair(ccw, cw)
    }

    val ccwIndex = (currentPerimeterIndex - 1 + totalPerimeter) % totalPerimeter
    val cwIndex = (currentPerimeterIndex + 1) % totalPerimeter

    return Pair(perimeterSeats[ccwIndex], perimeterSeats[cwIndex])
}

/**
 * Resolves perimeter clockwise and counter-clockwise adjacent seats given total perimeter seat count.
 * Returns Pair(leftNeighborIndex, rightNeighborIndex) using modular arithmetic.
 */
fun Seat.neighbors(totalSeats: Int): Pair<Int, Int> {
    require(totalSeats > 0) { "Total seats must be greater than 0" }
    val ccwIndex = (this.index - 1 + totalSeats) % totalSeats
    val cwIndex = (this.index + 1) % totalSeats
    return Pair(ccwIndex, cwIndex)
}
