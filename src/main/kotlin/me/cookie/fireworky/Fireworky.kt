package me.cookie.fireworky

import co.aikar.commands.PaperCommandManager
import me.cookie.fireworky.commands.FireworkyCommand
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class Fireworky: JavaPlugin(), Listener {
    lateinit var fireworkManager: FireworkManager
    override fun onEnable() {
        saveDefaultConfig()

        ConfigValues.maxEditsBeforeAutoSave = config.getInt("max-edits-before-auto-save", 15)

        fireworkManager = FireworkManager(this)
        server.pluginManager.registerEvents(this, this)
        val paperCommandManager = PaperCommandManager(this)
        paperCommandManager.registerCommand(FireworkyCommand(this))
        paperCommandManager.commandCompletions.registerAsyncCompletion("fireworks") {
            fireworkManager.fireworks().keys
        }

        paperCommandManager.commandCompletions.registerAsyncCompletion("xs") {
            listOf(it.player.location.x.toString())
        }
        paperCommandManager.commandCompletions.registerAsyncCompletion("ys") {
            listOf(it.player.location.y.toString())
        }
        paperCommandManager.commandCompletions.registerAsyncCompletion("zs") {
            listOf(it.player.location.z.toString())
        }

        dataFolder.mkdirs()

        fireworkManager.loadFireworks()
    }

    override fun onDisable() {
        //TODO: Save firework data
    }
}