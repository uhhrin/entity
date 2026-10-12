package org.thatnubarin.entity.core

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.thatnubarin.entity.config.ConfigManager
import org.thatnubarin.entity.handlers.HauntSequenceHandler

class HauntManager(
    private val plugin: org.bukkit.plugin.Plugin,
    private val config: ConfigManager,
    private val tracker: PlayerTracker,
    private val scheduler: HauntScheduler,
    private val sequence: HauntSequenceHandler
) {
    private val debugEntities = mutableMapOf<java.util.UUID, org.thatnubarin.entity.entity.HerobrineEntity>()
    private val tempEntities = mutableListOf<org.thatnubarin.entity.entity.HerobrineEntity>()

    fun startFor(player: Player) {
        if (!config.enabled) return
        if (player.hasPermission("entity.bypass")) return
        tracker.addPlayer(player.uniqueId)
        scheduler.startHaunting(player) { target -> executeHaunt(target) }
    }

    fun forceHaunt(player: Player) {
        if (!config.enabled) return
        tracker.addPlayer(player.uniqueId)
        scheduler.cancel(player)
        tracker.markStarted(player.uniqueId)
        Bukkit.getScheduler().runTask(plugin, Runnable { executeHaunt(player) })
    }

    fun stopFor(player: Player) {
        scheduler.cancel(player)
        tracker.markPaused(player.uniqueId)
    }

    fun resumeFor(player: Player) {
        startFor(player)
    }

    fun executeHaunt(player: Player) {
        if (!player.isOnline) return
        if (player.gameMode.name.equals("SPECTATOR", true) ||
            player.gameMode.name.equals("CREATIVE", true)
        ) {
            tracker.markPaused(player.uniqueId)
            scheduler.scheduleNext(player) { executeHaunt(it) }
            return
        }
        sequence.executeHaunt(player) {
            tracker.markCompleted(player.uniqueId)
            scheduler.scheduleNext(player) { executeHaunt(it) }
        }
    }

    fun cleanupAll() {
        debugEntities.values.forEach { it.despawn() }
        debugEntities.clear()
        tempEntities.forEach { it.despawn() }
        tempEntities.clear()
    }

    fun spawnDebug(player: Player): Boolean {
        val entity = org.thatnubarin.entity.entity.HerobrineEntity(plugin, config)
        val loc = player.location.clone().add(player.location.direction.normalize().multiply(2.0))
        entity.spawn(loc)
        entity.setVisible(true)
        debugEntities[player.uniqueId]?.despawn()
        debugEntities[player.uniqueId] = entity
        return true
    }

    fun despawnDebug(player: Player): Boolean {
        val removed = debugEntities.remove(player.uniqueId)
        removed?.despawn()
        return removed != null
    }

    fun showSleepWatcher(player: Player) {
        if (!config.enabled) return
        if (player.hasPermission("entity.bypass")) return
        val watcher = org.thatnubarin.entity.entity.HerobrineEntity(plugin, config)
        val lookDir = player.location.direction.normalize()
        val loc = player.location.clone().add(lookDir.multiply(1.5)).apply { y = player.location.y }
        watcher.spawn(loc)
        watcher.setVisible(true)
        tempEntities.add(watcher)
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            watcher.despawn()
            tempEntities.remove(watcher)
        }, 60L) // 3 seconds
    }
}
