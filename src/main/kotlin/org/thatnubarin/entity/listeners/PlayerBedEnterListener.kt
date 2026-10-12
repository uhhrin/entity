package org.thatnubarin.entity.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent
import org.thatnubarin.entity.core.HauntManager

class PlayerBedEnterListener(
    private val hauntManager: HauntManager
) : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onBedEnter(event: PlayerBedEnterEvent) {
        hauntManager.showSleepWatcher(event.player)
    }
}
