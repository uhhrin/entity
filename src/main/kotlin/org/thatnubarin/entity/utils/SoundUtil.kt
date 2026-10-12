package org.thatnubarin.entity.utils

import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.plugin.Plugin

object SoundUtil {
    fun playAttack(plugin: Plugin, location: Location) = play(plugin, location, "sounds.attack")
    fun playDisappear(plugin: Plugin, location: Location) = play(plugin, location, "sounds.disappear")
    fun playAppear(plugin: Plugin, location: Location) = play(plugin, location, "sounds.appear")
    fun playCurse(plugin: Plugin, location: Location) = play(plugin, location, "sounds.curse")
    fun playAmbient(plugin: Plugin, location: Location) = play(plugin, location, "sounds.ambient")

    private fun play(plugin: Plugin, location: Location, path: String) {
        if (!plugin.config.getBoolean("sounds.enabled", true)) return
        val soundName = plugin.config.getString(path, "") ?: return
        if (soundName.isBlank()) return
        val volume = plugin.config.getDouble("sounds.volume", 1.0).toFloat()
        val pitch = plugin.config.getDouble("sounds.pitch", 1.0).toFloat()
        val sound = runCatching { Sound.valueOf(soundName) }.getOrNull() ?: return
        location.world?.playSound(location, sound, volume, pitch)
    }
}
