package com.mimikunda.nightwatch.domain.model

data class PerimeterSeatingTable(
    val topCount: Int = 0,
    val rightCount: Int = 0,
    val bottomCount: Int = 0,
    val leftCount: Int = 0,
    val floatingCount: Int = 0,
    val seats: List<Seat> = emptyList()
) {
    val totalPerimeterSeats: Int
        get() = topCount + rightCount + bottomCount + leftCount

    val totalSeats: Int
        get() = totalPerimeterSeats + floatingCount

    companion object {
        fun build(
            topCount: Int = 0,
            rightCount: Int = 0,
            bottomCount: Int = 0,
            leftCount: Int = 0,
            floatingCount: Int = 0,
            customNames: List<String?> = emptyList(),
            customRoles: List<String?> = emptyList()
        ): PerimeterSeatingTable {
            val seatsList = mutableListOf<Seat>()
            var currentIndex = 0

            // TOP edge: left to right
            for (i in 0 until topCount) {
                val seatId = "seat_$currentIndex"
                seatsList.add(
                    Seat(
                        id = seatId,
                        index = currentIndex,
                        name = customNames.getOrNull(currentIndex),
                        roleId = customRoles.getOrNull(currentIndex),
                        position = SeatPosition(TableEdge.TOP, i)
                    )
                )
                currentIndex++
            }

            // RIGHT edge: top to bottom
            for (i in 0 until rightCount) {
                val seatId = "seat_$currentIndex"
                seatsList.add(
                    Seat(
                        id = seatId,
                        index = currentIndex,
                        name = customNames.getOrNull(currentIndex),
                        roleId = customRoles.getOrNull(currentIndex),
                        position = SeatPosition(TableEdge.RIGHT, i)
                    )
                )
                currentIndex++
            }

            // BOTTOM edge: right to left
            for (i in 0 until bottomCount) {
                val seatId = "seat_$currentIndex"
                seatsList.add(
                    Seat(
                        id = seatId,
                        index = currentIndex,
                        name = customNames.getOrNull(currentIndex),
                        roleId = customRoles.getOrNull(currentIndex),
                        position = SeatPosition(TableEdge.BOTTOM, i)
                    )
                )
                currentIndex++
            }

            // LEFT edge: bottom to top
            for (i in 0 until leftCount) {
                val seatId = "seat_$currentIndex"
                seatsList.add(
                    Seat(
                        id = seatId,
                        index = currentIndex,
                        name = customNames.getOrNull(currentIndex),
                        roleId = customRoles.getOrNull(currentIndex),
                        position = SeatPosition(TableEdge.LEFT, i)
                    )
                )
                currentIndex++
            }

            // FLOATING
            for (i in 0 until floatingCount) {
                val seatId = "seat_$currentIndex"
                seatsList.add(
                    Seat(
                        id = seatId,
                        index = currentIndex,
                        name = customNames.getOrNull(currentIndex),
                        roleId = customRoles.getOrNull(currentIndex),
                        position = SeatPosition(TableEdge.FLOATING, i)
                    )
                )
                currentIndex++
            }

            return PerimeterSeatingTable(
                topCount = topCount,
                rightCount = rightCount,
                bottomCount = bottomCount,
                leftCount = leftCount,
                floatingCount = floatingCount,
                seats = seatsList
            )
        }
    }
}
