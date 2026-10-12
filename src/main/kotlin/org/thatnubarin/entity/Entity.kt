package org.thatnubarin.entity

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.thatnubarin.entity.commands.EntityCommand
import org.thatnubarin.entity.config.ConfigManager
import org.thatnubarin.entity.core.HauntManager
import org.thatnubarin.entity.core.HauntScheduler
import org.thatnubarin.entity.core.PlayerTracker
import org.thatnubarin.entity.handlers.HauntSequenceHandler
import org.thatnubarin.entity.commands.CommandRegistrar
import org.thatnubarin.entity.listeners.PlayerJoinListener
import org.thatnubarin.entity.listeners.PlayerQuitListener
import org.thatnubarin.entity.listeners.PlayerBedEnterListener

class Entity : JavaPlugin() {
    private lateinit var configManager: ConfigManager
    private lateinit var playerTracker: PlayerTracker
    private lateinit var hauntScheduler: HauntScheduler
    private lateinit var hauntManager: HauntManager
    private lateinit var hauntSequenceHandler: HauntSequenceHandler

    override fun onEnable() {
        saveDefaultConfig()
        configManager = ConfigManager(this)
        playerTracker = PlayerTracker()
        hauntScheduler = HauntScheduler(this, configManager, playerTracker)
        hauntSequenceHandler = HauntSequenceHandler(this, configManager, hauntScheduler, playerTracker)
        hauntManager = HauntManager(this, configManager, playerTracker, hauntScheduler, hauntSequenceHandler)

        registerListeners()
        registerCommands()

        // Start haunting for players already online during reloads
        if (configManager.autoStartOnJoin) {
            Bukkit.getOnlinePlayers().forEach { hauntManager.startFor(it) }
        }

        logger.info("Entity plugin enabled")
    }

    override fun onDisable() {
        hauntScheduler.cancelAll()
        hauntManager.cleanupAll()
        logger.info("Entity plugin disabled")
    }

    private fun registerListeners() {
        val pm = server.pluginManager
        pm.registerEvents(PlayerJoinListener(hauntManager, configManager), this)
        pm.registerEvents(PlayerQuitListener(hauntManager, hauntScheduler, playerTracker), this)
        pm.registerEvents(org.thatnubarin.entity.listeners.PlayerDeathListener(this, configManager), this)
        pm.registerEvents(PlayerBedEnterListener(hauntManager), this)
    }

    private fun registerCommands() {
        val root = EntityCommand(this, hauntManager, hauntScheduler, playerTracker, configManager)
        CommandRegistrar.register(this, root)
    }
}
