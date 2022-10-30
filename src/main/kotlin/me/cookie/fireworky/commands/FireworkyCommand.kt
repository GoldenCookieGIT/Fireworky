package me.cookie.fireworky.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.cookie.fireworky.ConfigValues
import me.cookie.fireworky.Fireworky
import me.cookie.fireworky.colorize
import me.cookie.fireworky.gui.FireworkyMenu
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@Suppress("unused")
@CommandAlias("fireworky|fwy")
class FireworkyCommand(private val plugin: Fireworky): BaseCommand() {

    @Default
    @CommandPermission("fireworky.command")
    @Description("Opens the firework editor")
    fun onDefault(sender: Player) {
        FireworkyMenu(plugin.fireworkManager).show(sender)
    }

    @Subcommand("info|help")
    @CommandPermission("fireworky.info")
    @Description("Shows the plugin info")
    fun onInfo(sender: CommandSender) {
        sender.sendMessage("Running Fireworky version v${plugin.description.version} by Cookie#8723")
    }

    @Subcommand("launch|l")
    @CommandPermission("fireworky.launch")
    @CommandCompletion("@fireworks")
    @Syntax("<firework>")
    @Description("Launch a firework with given ID")
    fun onLaunch(sender: Player, firework: String) {
        if (!plugin.fireworkManager.launchFirework(firework, sender.location))
            sender.sendMessage(colorize("&cFailed to launch firework $firework, does it exist?"))
    }

    @Subcommand("add")
    @CommandPermission("fireworky.add")
    @Description("Add a firework to the list of fireworks with a specified name")
    fun onAdd(sender: CommandSender, firework: String) {
        var fireworkName = firework
        if (fireworkName.length == 1) {
            fireworkName = UUID.randomUUID().toString().take(6)
        }
        plugin.fireworkManager.addFirework(fireworkName, plugin.fireworkManager.dummyFireworkMeta)
    }

    @Subcommand("launchatloc|latl")
    @CommandPermission("fireworky.launchatlocation")
    @CommandCompletion("@fireworks @worlds @xs @ys @zs")
    @Syntax("<fireworks> <world> <x> <y> <z>")
    @Description("Launch a firework with given ID at a specified location")
    fun onLaunchAtlocation(sender: CommandSender,
                           firework: String,
                           world: String,
                           x: Double,
                           y: Double,
                           z: Double) {
        val bukkitWorld = Bukkit.getWorld(world) ?: Bukkit.getWorlds().first()
        if (!plugin.fireworkManager.launchFirework(firework, Location(bukkitWorld, x, y, z)))
            sender.sendMessage(colorize("&cFailed to launch firework $firework, does it exist?"))
    }

    @Subcommand("give")
    @CommandPermission("fireworky.give")
    @CommandCompletion("@players @fireworks")
    @Syntax("<player> <firework>")
    @Description("Give a player a firework")
    fun onGive(sender: CommandSender, player: String, firework: String) {
        val bukkitPlayer = Bukkit.getPlayer(player)
            ?: return sender.sendMessage(colorize("&cPlayer $player not found"))
        plugin.fireworkManager.giveFirework(bukkitPlayer, firework)
    }

    @Subcommand("reload")
    @CommandPermission("fireworky.reload")
    @Description("Reload the config")
    fun onReload(sender: CommandSender) {
        ConfigValues.maxEditsBeforeAutoSave = plugin.config.getInt("max-edits-before-auto-saving", 15)
        sender.sendMessage(colorize("&aReloaded the config!"))
    }
}