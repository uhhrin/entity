package org.thatnubarin.entity.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.thatnubarin.entity.config.ConfigManager
import org.thatnubarin.entity.handlers.DamageHandler

class PlayerDeathListener(
    private val plugin: org.bukkit.plugin.Plugin,
    private val config: ConfigManager
) : Listener {

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        if (!config.noLootOnDeath) return
        if (DamageHandler.isHauntDeath(player)) {
            event.keepInventory = true
            event.drops.clear()
            event.droppedExp = 0
            DamageHandler.clearHauntDeath(plugin, player)
        }
    }
}
