package org.thatnubarin.entity.core

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.thatnubarin.entity.config.ConfigManager

class HauntScheduler(
    private val plugin: org.bukkit.plugin.Plugin,
    private val config: ConfigManager,
    private val tracker: PlayerTracker
) {
    private val tasks = mutableMapOf<java.util.UUID, BukkitTask>()
    private val random = java.util.concurrent.ThreadLocalRandom.current()

    fun startHaunting(player: Player, execute: (Player) -> Unit) {
        if (!config.enabled) return
        tracker.addPlayer(player.uniqueId)
        schedule(player, config.firstHauntDelaySeconds, execute)
    }

    fun scheduleNext(player: Player, execute: (Player) -> Unit) {
        schedule(player, 0, execute)
    }

    private fun schedule(player: Player, baseDelaySeconds: Int, execute: (Player) -> Unit) {
        cancel(player)
        if (!config.enabled) return

        val min = config.minIntervalSeconds
        val max = config.maxIntervalSeconds
        val randomDelay = random.nextInt(min, max + 1)
        val totalSeconds = baseDelaySeconds + randomDelay

        tracker.markScheduled(player.uniqueId, System.currentTimeMillis() + totalSeconds * 1000L)

        val task = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (!player.isOnline) return@Runnable
            tracker.markStarted(player.uniqueId)
            execute(player)
        }, totalSeconds * 20L)

        tasks[player.uniqueId] = task
    }

    fun cancel(player: Player) {
        tasks.remove(player.uniqueId)?.cancel()
    }

    fun cancelAll() {
        tasks.values.forEach { it.cancel() }
        tasks.clear()
    }
}
