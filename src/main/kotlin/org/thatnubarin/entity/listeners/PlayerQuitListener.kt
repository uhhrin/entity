package org.thatnubarin.entity.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.thatnubarin.entity.core.HauntManager
import org.thatnubarin.entity.core.HauntScheduler
import org.thatnubarin.entity.core.PlayerTracker

class PlayerQuitListener(
    private val hauntManager: HauntManager,
    private val scheduler: HauntScheduler,
    private val tracker: PlayerTracker
) : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        scheduler.cancel(player)
        tracker.removePlayer(player.uniqueId)
        hauntManager.cleanupAll()
    }
}
