package org.thatnubarin.entity.entity

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector
import org.thatnubarin.entity.config.ConfigManager
import java.util.UUID

class HerobrineEntity(
    private val plugin: Plugin,
    private val config: ConfigManager
) {
    private var armorStand: ArmorStand? = null

    fun spawn(location: Location) {
        despawn()
        val world: World = location.world ?: return
        armorStand = world.spawn(location, ArmorStand::class.java) { stand ->
            stand.isInvisible = false
            stand.isMarker = false
            stand.setArms(true)
            stand.isSmall = false
            stand.isCustomNameVisible = false
            stand.setGravity(false)
            stand.isInvulnerable = true
            stand.isCollidable = false
            stand.setBasePlate(false)
            stand.isGlowing = true
            applyEquipment(stand)
        }
    }

    fun despawn() {
        armorStand?.remove()
        armorStand = null
    }

    fun teleport(location: Location) {
        armorStand?.teleport(location)
    }

    fun setVisible(visible: Boolean) {
        armorStand?.isInvisible = !visible
    }

    fun teleportBehind(player: Player, distance: Double) {
        val loc = calculateBehind(player, distance)
        armorStand?.teleport(loc)
    }

    private fun calculateBehind(player: Player, distance: Double): Location {
        val playerLoc = player.location
        val direction: Vector = playerLoc.direction.normalize()
        val behind = direction.multiply(-distance)
        return playerLoc.clone().add(behind)
    }

    private fun applyEquipment(stand: ArmorStand) {
        val equipment = stand.equipment ?: return
        equipment.helmet = buildHead()
        equipment.chestplate = leatherArmor(Material.LEATHER_CHESTPLATE)
        equipment.leggings = leatherArmor(Material.LEATHER_LEGGINGS)
        equipment.boots = leatherArmor(Material.LEATHER_BOOTS)
        equipment.setItemInMainHand(ItemStack(Material.AIR))
        equipment.setItemInOffHand(ItemStack(Material.AIR))
    }

    private fun leatherArmor(type: Material): ItemStack {
        val item = ItemStack(type)
        val meta = item.itemMeta as LeatherArmorMeta
        meta.setColor(Color.fromRGB(20, 20, 20))
        meta.isUnbreakable = true
        meta.addItemFlags(*org.bukkit.inventory.ItemFlag.entries.toTypedArray())
        item.itemMeta = meta
        return item
    }

    private fun buildHead(): ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD)
        val meta = head.itemMeta as SkullMeta
        val profile: PlayerProfile = Bukkit.createProfile(UUID.randomUUID(), "Herobrine")
        val value = config.skinValue
        val signature = config.skinSignature
        if (!value.isNullOrBlank()) {
            if (!signature.isNullOrBlank()) {
                profile.properties.add(ProfileProperty("textures", value, signature))
            } else {
                profile.properties.add(ProfileProperty("textures", value))
            }
        }
        meta.playerProfile = profile
        head.itemMeta = meta
        return head
    }
}
