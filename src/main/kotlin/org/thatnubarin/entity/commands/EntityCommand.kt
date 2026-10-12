package org.thatnubarin.entity.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.thatnubarin.entity.config.ConfigManager
import org.thatnubarin.entity.core.HauntManager
import org.thatnubarin.entity.core.HauntScheduler
import org.thatnubarin.entity.core.PlayerTracker
import java.util.Locale

class EntityCommand(
    private val plugin: org.bukkit.plugin.Plugin,
    private val hauntManager: HauntManager,
    private val scheduler: HauntScheduler,
    private val tracker: PlayerTracker,
    private val config: ConfigManager
) : Command("entity", "Entity plugin commands", "/entity", listOf("ent", "herobrine")) {

    override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("/$label [force|stop|start|status|reload|toggle] [player]")
            return true
        }
        when (args[0].lowercase(Locale.getDefault())) {
            "force" -> {
                if (!sender.hasPermission("entity.force")) {
                    sender.sendMessage("No permission.")
                    return true
                }
                val player = lookupPlayer(args.getOrNull(1), sender) ?: return true
                hauntManager.forceHaunt(player)
                sender.sendMessage(config.forceHauntMessage.replace("%player%", player.name))
            }
            "stop" -> {
                if (!sender.hasPermission("entity.admin")) {
                    sender.sendMessage("No permission.")
                    return true
                }
                val player = lookupPlayer(args.getOrNull(1), sender) ?: return true
                hauntManager.stopFor(player)
                sender.sendMessage(config.hauntStoppedMessage.replace("%player%", player.name))
            }
            "start" -> {
                if (!sender.hasPermission("entity.admin")) {
                    sender.sendMessage("No permission.")
                    return true
                }
                val player = lookupPlayer(args.getOrNull(1), sender) ?: return true
                hauntManager.resumeFor(player)
                sender.sendMessage(config.hauntStartedMessage.replace("%player%", player.name))
            }
            "status" -> {
                if (!sender.hasPermission("entity.admin")) {
                    sender.sendMessage("No permission.")
                    return true
                }
                val target = lookupPlayer(args.getOrNull(1), sender) ?: sender as? Player ?: return true
                val data = tracker.getData(target.uniqueId)
                sender.sendMessage("===== ${target.name}'s Haunt Status =====")
                sender.sendMessage("Currently being haunted: ${data?.currentlyBeingHaunted ?: false}")
                val nextSeconds = data?.nextHauntTime?.let { (it - System.currentTimeMillis()).coerceAtLeast(0) / 1000 }
                sender.sendMessage("Next haunt in: ${nextSeconds ?: "n/a"} seconds")
                sender.sendMessage("Total haunts received: ${data?.totalHaunts ?: 0}")
                sender.sendMessage("Last haunt: ${data?.lastHauntTime ?: 0}")
            }
            "reload" -> {
                if (!sender.hasPermission("entity.admin")) {
                    sender.sendMessage("No permission.")
                    return true
                }
                plugin.reloadConfig()
                sender.sendMessage("Entity config reloaded.")
            }
            "toggle" -> {
                if (!sender.hasPermission("entity.admin")) {
                    sender.sendMessage("No permission.")
                    return true
                }
                val enabled = plugin.config.getBoolean("haunt.enabled", true)
                plugin.config.set("haunt.enabled", !enabled)
                plugin.saveConfig()
                sender.sendMessage("Haunting globally ${if (!enabled) "enabled" else "disabled"}.")
            }
            "debugspawn" -> {
                if (!sender.hasPermission("entity.admin")) {
                    sender.sendMessage("No permission.")
                    return true
                }
                val player = sender as? Player ?: run {
                    sender.sendMessage("Only players can spawn debug entity.")
                    return true
                }
                hauntManager.spawnDebug(player)
                sender.sendMessage("Spawned debug entity in front of you.")
            }
            "debugdespawn" -> {
                if (!sender.hasPermission("entity.admin")) {
                    sender.sendMessage("No permission.")
                    return true
                }
                val player = sender as? Player ?: run {
                    sender.sendMessage("Only players can despawn debug entity.")
                    return true
                }
                if (hauntManager.despawnDebug(player)) {
                    sender.sendMessage("Debug entity despawned.")
                } else {
                    sender.sendMessage("No debug entity to despawn.")
                }
            }
            else -> sender.sendMessage("Unknown subcommand.")
        }
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        if (args.isEmpty()) return mutableListOf("force", "stop", "start", "status", "reload", "toggle", "debugspawn", "debugdespawn")
        if (args.size == 1) {
            return mutableListOf("force", "stop", "start", "status", "reload", "toggle", "debugspawn", "debugdespawn")
        }
        if (args.size == 2 && args[0].lowercase(Locale.getDefault()) in setOf("force", "stop", "start", "status")) {
            return Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
        }
        return mutableListOf()
    }

    private fun lookupPlayer(name: String?, sender: CommandSender): Player? {
        if (name == null) {
            sender.sendMessage("Specify a player.")
            return null
        }
        val player = Bukkit.getPlayerExact(name)
        if (player == null) {
            sender.sendMessage("Player not found.")
            return null
        }
        return player
    }
}
