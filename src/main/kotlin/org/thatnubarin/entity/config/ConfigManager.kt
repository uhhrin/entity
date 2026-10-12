package org.thatnubarin.entity.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class ConfigManager(private val plugin: JavaPlugin) {
    private val config: FileConfiguration
        get() = plugin.config

    val enabled: Boolean
        get() = config.getBoolean("haunt.enabled", true)

    val autoStartOnJoin: Boolean
        get() = config.getBoolean("haunt.auto-start-on-join", true)

    val firstHauntDelaySeconds: Int
        get() = config.getInt("haunt.first-haunt-delay", 120)

    val minIntervalSeconds: Int
        get() = config.getInt("haunt.min-interval", 60)

    val maxIntervalSeconds: Int
        get() = config.getInt("haunt.max-interval", 600)

    val damagePerHit: Double
        get() = config.getDouble("entity.damage", 2.0)

    val flashDurationTicks: Long
        get() = config.getLong("entity.flash-duration", 15L)

    val spawnDistance: Double
        get() = config.getDouble("entity.spawn-distance", 4.0)

    val noLootOnDeath: Boolean
        get() = config.getBoolean("entity.no-loot-on-death", true)

    val soundsEnabled: Boolean
        get() = config.getBoolean("sounds.enabled", true)

    val hauntTitle: String
        get() = config.getString("messages.haunt-title", "&4&lI am watching you...") ?: "&4&lI am watching you..."

    val hauntSubtitle: String
        get() = config.getString("messages.haunt-subtitle", "") ?: ""

    val forceHauntMessage: String
        get() = config.getString("messages.forced-haunt", "&6Haunt forced on %player%") ?: "&6Haunt forced on %player%"

    val hauntStoppedMessage: String
        get() = config.getString("messages.haunt-stopped", "&aHaunting stopped for %player%") ?: "&aHaunting stopped for %player%"

    val hauntStartedMessage: String
        get() = config.getString("messages.haunt-started", "&aHaunting resumed for %player%") ?: "&aHaunting resumed for %player%"

    val skinValue: String?
        get() = config.getString("entity.skin.value", null)

    val skinSignature: String?
        get() = config.getString("entity.skin.signature", null)

    fun reload() {
        plugin.reloadConfig()
    }
}
