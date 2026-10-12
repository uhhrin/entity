package org.thatnubarin.entity.core

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PlayerTracker {
    data class PlayerHauntData(
        var lastHauntTime: Long = 0L,
        var nextHauntTime: Long = 0L,
        var totalHaunts: Int = 0,
        var currentlyBeingHaunted: Boolean = false,
        var state: HauntState = HauntState.IDLE
    )

    enum class HauntState {
        IDLE, SCHEDULED, IN_PROGRESS, PAUSED
    }

    private val data = ConcurrentHashMap<UUID, PlayerHauntData>()

    fun addPlayer(playerId: UUID) {
        data.putIfAbsent(playerId, PlayerHauntData())
    }

    fun removePlayer(playerId: UUID) {
        data.remove(playerId)
    }

    fun getData(playerId: UUID): PlayerHauntData? = data[playerId]

    fun markScheduled(playerId: UUID, nextTime: Long) {
        val entry = data.computeIfAbsent(playerId) { PlayerHauntData() }
        entry.state = HauntState.SCHEDULED
        entry.nextHauntTime = nextTime
    }

    fun markStarted(playerId: UUID) {
        data[playerId]?.apply {
            state = HauntState.IN_PROGRESS
            currentlyBeingHaunted = true
        }
    }

    fun markCompleted(playerId: UUID) {
        data[playerId]?.apply {
            state = HauntState.IDLE
            currentlyBeingHaunted = false
            lastHauntTime = System.currentTimeMillis()
            totalHaunts += 1
        }
    }

    fun markPaused(playerId: UUID) {
        data[playerId]?.apply {
            state = HauntState.PAUSED
            currentlyBeingHaunted = false
        }
    }
}
