package org.thatnubarin.entity.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.thatnubarin.entity.config.ConfigManager
import org.thatnubarin.entity.core.HauntManager

class PlayerJoinListener(
    private val hauntManager: HauntManager,
    private val config: ConfigManager
) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (!config.autoStartOnJoin || !config.enabled) return
        hauntManager.startFor(event.player)
    }
}
