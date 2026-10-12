package org.thatnubarin.entity.utils

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.thatnubarin.entity.config.ConfigManager

object MessageUtil {
    private fun translate(input: String): String = ChatColor.translateAlternateColorCodes('&', input)

    fun sendActionBar(player: Player, config: ConfigManager) {
        player.sendActionBar(translate(config.hauntTitle))
    }

    fun send(player: Player, message: String) {
        player.sendMessage(translate(message))
    }
}
