package org.thatnubarin.entity.handlers

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector

object DamageHandler {
    private const val META_KEY = "entity_death"

    fun dealHauntDamage(plugin: Plugin, target: Player, damage: Double) {
        if (!target.isOnline) return
        if (target.gameMode == GameMode.CREATIVE || target.gameMode == GameMode.SPECTATOR) return
        if (target.isDead || target.health <= 0) return

        // prevent knockback by resetting velocity
        target.damage(damage)
        target.velocity = Vector(0, 0, 0)

        if (target.health - damage <= 0) {
            target.setMetadata(META_KEY, FixedMetadataValue(plugin, true))
        }
    }

    fun isHauntDeath(player: Player): Boolean = player.hasMetadata(META_KEY)

    fun clearHauntDeath(plugin: Plugin, player: Player) {
        player.removeMetadata(META_KEY, plugin)
    }
}
