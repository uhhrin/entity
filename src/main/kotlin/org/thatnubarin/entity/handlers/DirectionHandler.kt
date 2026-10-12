package org.thatnubarin.entity.handlers

import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.plugin.Plugin
import kotlin.math.abs

class DirectionHandler(
    private val plugin: Plugin,
    private val player: Player,
    private val onTurnAround: () -> Unit,
    private val onTurnBack: () -> Unit
) {
    private val initialYaw = player.location.yaw
    private var turnedAround = false

    fun start() {
        object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline) {
                    cancel()
                    return
                }
                val currentYaw = player.location.yaw
                val yawDiff = abs(normalize(currentYaw - initialYaw))

                if (!turnedAround && yawDiff > 90f) {
                    turnedAround = true
                    onTurnAround()
                }

                if (turnedAround && yawDiff < 45f) {
                    onTurnBack()
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    private fun normalize(yaw: Float): Float {
        var value = yaw
        while (value > 180f) value -= 360f
        while (value < -180f) value += 360f
        return value
    }
}
