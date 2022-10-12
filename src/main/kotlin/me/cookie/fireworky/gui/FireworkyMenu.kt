package me.cookie.fireworky.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.cookie.fireworky.*
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class FireworkyMenu(
    private val fireworkManager: FireworkManager,
): MenuGui(6, "Fireworky Menu") {
    private var page = 0
    private val lightFiller = filler(Material.GRAY_STAINED_GLASS_PANE)
    private val fireworkPane = StaticPane(1, 1, 7, 4)

    init {
        fireworkPane.priority = Pane.Priority.HIGHEST

        addPane(fireworkPane)
    }

    override fun setItems() {
        (0..28).forEach { index ->
            val pageIndex = 28 * page + index

            if (fireworkManager.fireworks().keys.toTypedArray().size <= pageIndex) {
                fireworkPane.addItem(GuiItem(lightFiller.item) { event ->
                    event.isCancelled = true }, toXY(index, 7).first, toXY(index, 7).second)
                return@forEach
            }

            val fireworkId = fireworkManager.fireworks().keys.toTypedArray()[pageIndex]

            fireworkPane.addItem(GuiItem(ItemStack(Material.FIREWORK_ROCKET).apply {
                itemMeta = itemMeta!!.apply {
                    lore = colorizeList(
                        "&aClick to edit the firework",
                        "&cRight click to remove the firework",
                        "&eMiddle click to get the launch command in chat",
                        "&7Firework ID: &e$fireworkId"
                    )
                }
            }) { event ->
                event.isCancelled = true

                if (event.whoClicked !is Player) return@GuiItem
                val player = event.whoClicked as Player

                if (event.isRightClick) {
                    fireworkManager.removeFirework(fireworkId)
                }

                if (event.click == ClickType.MIDDLE) {
                    val textComponent = TextComponent("Click me to copy launch command").apply {
                        clickEvent = ClickEvent(
                            ClickEvent.Action.SUGGEST_COMMAND,
                            "/fireworky launch $fireworkId"
                        )
                        color = ChatColor.YELLOW
                    }

                    player.spigot().sendMessage(textComponent)
                }

                if (event.isLeftClick) {
                    EditFireworkMenu(fireworkManager, fireworkId).show(player)
                    return@GuiItem
                }

                setAndUpdate()
            }, toXY(index, 7).first, toXY(index, 7).second)
        }

        if (page > 0) {
            basePane.addItem(GuiItem(ItemStack(Material.ARROW).apply {
                itemMeta = itemMeta!!.apply {
                    setDisplayName(colorize("&aPrevious page"))
                }
            }) { event ->
                event.isCancelled = true

                page--
                setAndUpdate()
            }, 0, 5)
        }

        if (page < fireworkManager.fireworks().keys.size / 28) {
            basePane.addItem(GuiItem(ItemStack(Material.ARROW).apply {
                itemMeta = itemMeta!!.apply {
                    setDisplayName(colorize("&r&aNext page"))
                }
            }) { event ->
                event.isCancelled = true

                page++
                setAndUpdate()
            }, 8, 5)
        }

        basePane.addItem(GuiItem(ItemStack(Material.PAPER).apply {
            itemMeta = itemMeta!!.clone().apply{ setDisplayName(colorize("&r&7Page: ${page + 1}")) }
        }) { event -> event.isCancelled = true }, 0, 0)

        basePane.addItem(GuiItem(ItemStack(Material.TURTLE_EGG).apply {
            itemMeta = itemMeta!!.clone().apply{ setDisplayName(colorize("&r&aNew Firework")) }
        }) { event ->
            event.isCancelled = true
            fireworkManager.addFirework(UUID.randomUUID().toString().take(6), fireworkManager.dummyFireworkMeta)

            setAndUpdate()
           }, 4, 5
        )
    }

    override val canGoBack = false
    override fun back(event: InventoryClickEvent) {}
}