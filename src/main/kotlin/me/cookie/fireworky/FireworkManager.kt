package me.cookie.fireworky

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.cookie.fireworky.serializers.FireworkMetaSerializer
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkMeta
import java.io.File
import kotlin.random.Random

class FireworkManager(private val plugin: Fireworky) {
    private val fireworks = mutableMapOf<String, FireworkMeta>()
    private val editedFireworks = mutableSetOf<String>()
    private var edits = 0

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(FireworkMeta::class.java, FireworkMetaSerializer(this))
        .setPrettyPrinting()
        .create()

    val dummyFireworkMeta: FireworkMeta
        get() = field.clone()

    init {
        dummyFireworkMeta = (Bukkit.getWorlds().first().spawnEntity(
            Bukkit.getWorlds().first().spawnLocation,
            EntityType.FIREWORK
        ) as Firework).fireworkMeta.clone()

        me.cookie.fireworky.gson = this.gson
    }

    fun addFirework(id: String, fireworkMeta: FireworkMeta) {
        fireworks[id] = fireworkMeta
        addEdit(id)
    }

    fun removeFirework(id: String) {
        fireworks.remove(id)
        addEdit(id)
    }

    fun launchFirework(id: String, location: Location): Boolean {
        val fireworkMeta = if(id == "random") {
            dummyFireworkMeta.apply {
                addEffect(randomEffect())
                power = (1..7).random()
            }
        } else {
            fireworks[id] ?: return false
        }

        val firework = location.world!!.spawnEntity(location, EntityType.FIREWORK) as Firework
        firework.fireworkMeta = fireworkMeta

        return true
    }

    fun editEffect(id: String, fwEffect: FireworkEffect, newFwEffect: FireworkEffect.Builder.() -> FireworkEffect.Builder):
            FireworkEffect {
        val newFireworkEffect = newFwEffect.invoke(FireworkEffect.builder()).build()
        addFirework(
            id,
            fireworks[id]!!.clone().apply {
                removeEffect(effects.indexOf(fwEffect))
                addEffect(
                    newFireworkEffect
                )
            }
        )

        return newFireworkEffect
    }

    fun fireworks(): Map<String, FireworkMeta> = fireworks

    private fun deleteFirework(id: String) {
        File(plugin.dataFolder, "$id.json").delete()
    }

    private fun saveFirework(id: String) {
        val fwJson = gson.toJson(fireworks[id]!!, FireworkMeta::class.java)

        val fireworkFile = File(plugin.dataFolder, "$id.json")

        fireworkFile.createNewFile()

        fireworkFile.writeText(fwJson)
    }

    private fun loadFirework(id: String, json: String) {
        fireworks[id] = gson.fromJson(json, FireworkMeta::class.java)
    }

    fun loadFireworks() {
        plugin.dataFolder.listFiles()?.forEach {
            if (it.extension == "json") {
                val id = it.nameWithoutExtension
                val json = it.readText()

                loadFirework(id, json)
            }
        }
    }

    fun saveFireworks() {
        editedFireworks.forEach { id ->
            val firework = fireworks[id]

            deleteFirework(id)

            if (firework == null) {
                return@forEach
            }

            saveFirework(id)
        }
        
        editedFireworks.clear()
    }
    
    private fun addEdit(id: String) {
        editedFireworks.add(id)
        edits++

        if(edits > ConfigValues.maxEditsBeforeAutoSave) {
            saveFireworks()
            edits = 0
        }
    }

    private val prettyColors = arrayOf(
        Color.WHITE,
        Color.SILVER,
        Color.GRAY,
        Color.BLACK,
        Color.RED,
        Color.MAROON,
        Color.YELLOW,
        Color.OLIVE,
        Color.LIME,
        Color.GREEN,
        Color.AQUA,
        Color.TEAL,
        Color.BLUE,
        Color.NAVY,
        Color.FUCHSIA,
        Color.PURPLE,
        Color.ORANGE
    )

    fun giveFirework(player: Player, fireworkId: String) {
        // The command can specify multiple commands, so need this to support that.
        fireworkId.split(" ").forEach { id ->
            val fireworkMeta = validateFirework(id) ?: return

            player.inventory.addItem(ItemStack(Material.FIREWORK_ROCKET).apply {
                itemMeta = fireworkMeta.apply {
                    lore = colorizeList(
                        "&7Firework ID: &e$id"
                    )
                }
            })
        }

    }

    private fun validateFirework(fireworkId: String): FireworkMeta? {
        return if(fireworkId == "random") {
            dummyFireworkMeta.apply {
                addEffect(randomEffect())
                power = (1..7).random()
            }
        } else {
            fireworks[fireworkId]
        }

    }

    private fun randomEffect(): FireworkEffect = FireworkEffect.builder()
            .with(FireworkEffect.Type.values().random())
            .withColor((0..(1..5).random()).map { prettyColors.random() })
            .withFade((0..(1..5).random()).map { prettyColors.random() })
            .trail(Random.nextBoolean())
            .flicker(Random.nextBoolean())
            .build()
}