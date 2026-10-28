package com.mimikunda.nightwatch

import com.mimikunda.nightwatch.domain.model.PerimeterSeatingTable
import com.mimikunda.nightwatch.domain.model.StatusMarker
import com.mimikunda.nightwatch.domain.reconciliation.DawnReconciler
import com.mimikunda.nightwatch.domain.reconciliation.NightAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DawnReconcilerTest {

    @Test
    fun `wolf target protected by bodyguard survives`() {
        val table = PerimeterSeatingTable.build(topCount = 4)
        val targetSeat = table.seats[0]

        val actions = listOf(
            NightAction.TargetByWolves(targetSeat.id),
            NightAction.ProtectByBodyguard(targetSeat.id)
        )

        val report = DawnReconciler.reconcileNight(table.seats, actions)

        assertTrue(report.eliminatedSeatIds.isEmpty())
        assertTrue(report.cascadeEliminatedSeatIds.isEmpty())
        assertTrue(report.savedSeatIds.contains(targetSeat.id))

        val updatedTarget = report.updatedSeats.first { it.id == targetSeat.id }
        assertTrue(updatedTarget.isAlive)
        assertFalse(updatedTarget.activeStatuses.contains(StatusMarker.WOLF_TARGET))
        assertFalse(updatedTarget.activeStatuses.contains(StatusMarker.PROTECTED))
    }

    @Test
    fun `wolf target saved by witch potion survives`() {
        val table = PerimeterSeatingTable.build(topCount = 4)
        val targetSeat = table.seats[1]

        val actions = listOf(
            NightAction.TargetByWolves(targetSeat.id),
            NightAction.SaveByPotion(targetSeat.id)
        )

        val report = DawnReconciler.reconcileNight(table.seats, actions)

        assertTrue(report.eliminatedSeatIds.isEmpty())
        assertTrue(report.savedSeatIds.contains(targetSeat.id))
    }

    @Test
    fun `unprotected wolf target dies`() {
        val table = PerimeterSeatingTable.build(topCount = 4)
        val targetSeat = table.seats[0]

        val actions = listOf(
            NightAction.TargetByWolves(targetSeat.id)
        )

        val report = DawnReconciler.reconcileNight(table.seats, actions)

        assertTrue(report.eliminatedSeatIds.contains(targetSeat.id))
        val updatedTarget = report.updatedSeats.first { it.id == targetSeat.id }
        assertFalse(updatedTarget.isAlive)
    }

    @Test
    fun `poisoned seat dies regardless of protection`() {
        val table = PerimeterSeatingTable.build(topCount = 4)
        val targetSeat = table.seats[2]

        val actions = listOf(
            NightAction.TargetByWolves(targetSeat.id),
            NightAction.SaveByPotion(targetSeat.id),
            NightAction.PoisonByWitch(targetSeat.id)
        )

        val report = DawnReconciler.reconcileNight(table.seats, actions)

        assertTrue(report.eliminatedSeatIds.contains(targetSeat.id))
        val updatedTarget = report.updatedSeats.first { it.id == targetSeat.id }
        assertFalse(updatedTarget.isAlive)
    }

    @Test
    fun `lover cascade elimination propagates when linked seat dies`() {
        val table = PerimeterSeatingTable.build(topCount = 4)
        val seatA = table.seats[0]
        val seatB = table.seats[1]

        // Link seatA and seatB as Lovers
        val linkedSeats = table.seats.map { seat ->
            when (seat.id) {
                seatA.id -> seat.copy(linkedSeatIds = setOf(seatB.id))
                seatB.id -> seat.copy(linkedSeatIds = setOf(seatA.id))
                else -> seat
            }
        }

        val actions = listOf(
            NightAction.TargetByWolves(seatA.id)
        )

        val report = DawnReconciler.reconcileNight(linkedSeats, actions)

        assertEquals(setOf(seatA.id), report.eliminatedSeatIds)
        assertEquals(setOf(seatB.id), report.cascadeEliminatedSeatIds)

        val updatedA = report.updatedSeats.first { it.id == seatA.id }
        val updatedB = report.updatedSeats.first { it.id == seatB.id }
        assertFalse(updatedA.isAlive)
        assertFalse(updatedB.isAlive)
    }

    @Test
    fun `multilevel cascade propagation eliminates all linked seats`() {
        val table = PerimeterSeatingTable.build(topCount = 4)
        val seatA = table.seats[0]
        val seatB = table.seats[1]
        val seatC = table.seats[2]

        // seatA linked to seatB, seatB linked to seatC
        val linkedSeats = table.seats.map { seat ->
            when (seat.id) {
                seatA.id -> seat.copy(linkedSeatIds = setOf(seatB.id))
                seatB.id -> seat.copy(linkedSeatIds = setOf(seatA.id, seatC.id))
                seatC.id -> seat.copy(linkedSeatIds = setOf(seatB.id))
                else -> seat
            }
        }

        val actions = listOf(
            NightAction.TargetByWolves(seatA.id)
        )

        val report = DawnReconciler.reconcileNight(linkedSeats, actions)

        assertEquals(setOf(seatA.id), report.eliminatedSeatIds)
        assertEquals(setOf(seatB.id, seatC.id), report.cascadeEliminatedSeatIds)
    }
}
