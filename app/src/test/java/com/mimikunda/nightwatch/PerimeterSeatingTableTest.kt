package com.mimikunda.nightwatch

import com.mimikunda.nightwatch.domain.model.PerimeterSeatingTable
import com.mimikunda.nightwatch.domain.model.TableEdge
import com.mimikunda.nightwatch.domain.model.neighbors
import org.junit.Assert.assertEquals
import org.junit.Test

class PerimeterSeatingTableTest {

    @Test
    fun `table building assigns correct edges and sequence indices`() {
        val table = PerimeterSeatingTable.build(
            topCount = 2,
            rightCount = 1,
            bottomCount = 2,
            leftCount = 1,
            floatingCount = 1
        )

        assertEquals(6, table.totalPerimeterSeats)
        assertEquals(7, table.totalSeats)
        assertEquals(7, table.seats.size)

        // Verify TOP seats (indices 0, 1)
        assertEquals(TableEdge.TOP, table.seats[0].position.edge)
        assertEquals(0, table.seats[0].position.positionOnEdge)
        assertEquals(0, table.seats[0].index)

        assertEquals(TableEdge.TOP, table.seats[1].position.edge)
        assertEquals(1, table.seats[1].position.positionOnEdge)
        assertEquals(1, table.seats[1].index)

        // Verify RIGHT seat (index 2)
        assertEquals(TableEdge.RIGHT, table.seats[2].position.edge)
        assertEquals(0, table.seats[2].position.positionOnEdge)
        assertEquals(2, table.seats[2].index)

        // Verify BOTTOM seats (indices 3, 4)
        assertEquals(TableEdge.BOTTOM, table.seats[3].position.edge)
        assertEquals(0, table.seats[3].position.positionOnEdge)
        assertEquals(3, table.seats[3].index)

        assertEquals(TableEdge.BOTTOM, table.seats[4].position.edge)
        assertEquals(1, table.seats[4].position.positionOnEdge)
        assertEquals(4, table.seats[4].index)

        // Verify LEFT seat (index 5)
        assertEquals(TableEdge.LEFT, table.seats[5].position.edge)
        assertEquals(0, table.seats[5].position.positionOnEdge)
        assertEquals(5, table.seats[5].index)

        // Verify FLOATING seat (index 6)
        assertEquals(TableEdge.FLOATING, table.seats[6].position.edge)
        assertEquals(0, table.seats[6].position.positionOnEdge)
        assertEquals(6, table.seats[6].index)
    }

    @Test
    fun `modular neighbor wrapping works correctly for perimeter seats`() {
        val table = PerimeterSeatingTable.build(
            topCount = 2,
            rightCount = 1,
            bottomCount = 2,
            leftCount = 1,
            floatingCount = 0
        )

        val totalPerimeter = table.totalPerimeterSeats // 6
        val seats = table.seats

        // Seat 0: left (CCW) is seat 5, right (CW) is seat 1
        val (ccw0, cw0) = seats[0].neighbors(seats)
        assertEquals(seats[5].id, ccw0.id)
        assertEquals(seats[1].id, cw0.id)

        // Int-based overload check for seat 0
        val (ccwIdx0, cwIdx0) = seats[0].neighbors(totalPerimeter)
        assertEquals(5, ccwIdx0)
        assertEquals(1, cwIdx0)

        // Seat 5 (last perimeter seat): CCW is seat 4, CW is seat 0
        val (ccw5, cw5) = seats[5].neighbors(seats)
        assertEquals(seats[4].id, ccw5.id)
        assertEquals(seats[0].id, cw5.id)

        val (ccwIdx5, cwIdx5) = seats[5].neighbors(totalPerimeter)
        assertEquals(4, ccwIdx5)
        assertEquals(0, cwIdx5)
    }
}
