package org.thatnubarin.entity.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.plugin.Plugin
import java.lang.reflect.Method

object CommandRegistrar {
    private var commandMap: CommandMap? = null
    private var lookedUp = false

    private fun resolveCommandMap(plugin: Plugin): CommandMap? {
        if (lookedUp) return commandMap
        lookedUp = true
        commandMap = runCatching {
            val server = plugin.server
            val method: Method = server.javaClass.getMethod("getCommandMap")
            method.isAccessible = true
            method.invoke(server) as CommandMap
        }.getOrNull()
        return commandMap
    }

    fun register(plugin: Plugin, command: Command) {
        val map = resolveCommandMap(plugin)
        map?.register(plugin.name.lowercase(), command)
            ?: plugin.logger.severe("Unable to register command ${command.name}: CommandMap not found")
    }
}
