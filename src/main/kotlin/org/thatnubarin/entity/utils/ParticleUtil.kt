package org.thatnubarin.entity.utils

import org.bukkit.Location
import org.bukkit.Particle
object ParticleUtil {
    fun spawnSmoke(plugin: org.bukkit.plugin.Plugin, location: Location) =
        spawn(plugin, location, "particles.spawn")

    fun spawnDisappear(plugin: org.bukkit.plugin.Plugin, location: Location) =
        spawn(plugin, location, "particles.despawn")

    fun spawnAppear(plugin: org.bukkit.plugin.Plugin, location: Location) =
        spawn(plugin, location, "particles.spawn")

    private fun spawn(plugin: org.bukkit.plugin.Plugin, location: Location, path: String) {
        if (!plugin.config.getBoolean("particles.enabled", true)) return
        val typeName = plugin.config.getString("$path.type", "") ?: return
        val count = plugin.config.getInt("$path.count", 10)
        val type = runCatching { Particle.valueOf(typeName) }.getOrNull() ?: return
        location.world?.spawnParticle(type, location, count, 0.5, 0.5, 0.5, 0.0)
    }
}
