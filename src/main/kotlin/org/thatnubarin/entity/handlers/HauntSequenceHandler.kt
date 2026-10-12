package org.thatnubarin.entity.handlers

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.thatnubarin.entity.config.ConfigManager
import org.thatnubarin.entity.entity.HerobrineEntity
import org.thatnubarin.entity.utils.MessageUtil
import org.thatnubarin.entity.utils.ParticleUtil
import org.thatnubarin.entity.utils.SoundUtil

class HauntSequenceHandler(
    private val plugin: Plugin,
    private val config: ConfigManager,
    private val scheduler: org.thatnubarin.entity.core.HauntScheduler,
    private val tracker: org.thatnubarin.entity.core.PlayerTracker
) {
    fun executeHaunt(target: Player, onComplete: () -> Unit) {
        if (!target.isOnline) return
        val entity = HerobrineEntity(plugin, config)

        // Phase 1: setup spawn behind player invisible
        val spawnLoc = target.location.clone()
        entity.spawn(spawnLoc)
        entity.setVisible(false)
        ParticleUtil.spawnSmoke(plugin, spawnLoc)

        // Phase 2: first strike after short delay
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (!target.isOnline) {
                entity.despawn(); onComplete(); return@Runnable
            }
            entity.teleportBehind(target, config.spawnDistance)
            entity.setVisible(false)
            SoundUtil.playAttack(plugin, target.location)
            DamageHandler.dealHauntDamage(plugin, target, config.damagePerHit)
        }, 10L)

        // Phase 3 & 4: direction tracking
        val directionHandler = DirectionHandler(plugin, target, onTurnAround = {
            entity.despawn()
            SoundUtil.playDisappear(plugin, target.location)
        }, onTurnBack = {
            // Phase 5: flash reappear
            val frontStart = target.location.clone().add(target.location.direction.normalize().multiply(2.0))
            val behindEnd = target.location.clone().add(target.location.direction.normalize().multiply(-config.spawnDistance))
            entity.spawn(frontStart)
            entity.setVisible(true)
            ParticleUtil.spawnAppear(plugin, target.location)
            SoundUtil.playAppear(plugin, target.location)

            passThroughPlayer(entity, target, frontStart, behindEnd)

            // Phase 6: second strike after flash duration
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                if (!target.isOnline) {
                    entity.despawn(); onComplete(); return@Runnable
                }
                DamageHandler.dealHauntDamage(plugin, target, config.damagePerHit)
                MessageUtil.sendActionBar(target, config)
                SoundUtil.playCurse(plugin, target.location)
                entity.despawn()
                onComplete()
            }, config.flashDurationTicks)
        })
        directionHandler.start()
    }

    private fun passThroughPlayer(
        entity: HerobrineEntity,
        player: Player,
        start: org.bukkit.Location,
        end: org.bukkit.Location
    ) {
        val steps = 6
        val dx = (end.x - start.x) / steps
        val dy = (end.y - start.y) / steps
        val dz = (end.z - start.z) / steps
        object : org.bukkit.scheduler.BukkitRunnable() {
            var tick = 0
            override fun run() {
                if (!player.isOnline) {
                    cancel()
                    entity.despawn()
                    return
                }
                if (tick >= steps) {
                    cancel()
                    entity.teleportBehind(player, config.spawnDistance)
                    return
                }
                val next = start.clone().add(dx * tick, dy * tick, dz * tick)
                entity.teleport(next)
                tick++
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }
}
