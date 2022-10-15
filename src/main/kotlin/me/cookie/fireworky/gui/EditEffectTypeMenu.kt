package me.cookie.fireworky.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.cookie.fireworky.*
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class EditEffectTypeMenu(
    private val fireworkManager: FireworkManager,
    private val fireworkId: String,
    effect: FireworkEffect,
): MenuGui(3, "Edit Effect Type", fireworkManager, fireworkId) {
    private val effectTypePane = StaticPane(1, 1, 7, 1)

    private var fwEffect = effect

    init {
        effectTypePane.priority = Pane.Priority.HIGHEST

        addPane(effectTypePane)
    }

    override fun setItems() {
        effectTypePane.fillWith(filler(Material.GRAY_STAINED_GLASS_PANE).item) { event -> event.isCancelled = true }

        effectTypePane.addItem(
            GuiItem(
                ItemStack(Material.SLIME_BALL).apply {itemMeta = itemMeta!!.apply {
                    setDisplayName(colorize("&rLarge Ball"))
                    addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    if (fwEffect.type == FireworkEffect.Type.BALL_LARGE) {
                        lore = colorizeList(
                            "&r&7SELECTED"
                        )
                        addEnchant(Enchantment.LUCK, 1, true)
                    }
                }}
            ) { event ->
                event.isCancelled = true

                fwEffect = fireworkManager
                    .editEffect(fireworkId, fwEffect) {
                        fwEffect.clone().with(FireworkEffect.Type.BALL_LARGE)
                    }

                setAndUpdate()
            }, 1, 0
        )

        effectTypePane.addItem(
            GuiItem(
                ItemStack(Material.SNOWBALL).apply {itemMeta = itemMeta!!.apply {
                    setDisplayName(colorize("&rBall"))
                    addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    if (fwEffect.type == FireworkEffect.Type.BALL) {
                        lore = colorizeList(
                            "&r&7SELECTED"
                        )
                        addEnchant(Enchantment.LUCK, 1, true)
                    }
                }}
            ) { event ->
                event.isCancelled = true

                fwEffect = fireworkManager
                    .editEffect(fireworkId, fwEffect) {
                        fwEffect.clone().with(FireworkEffect.Type.BALL)
                    }

                setAndUpdate()
            }, 2, 0
        )

        effectTypePane.addItem(
            GuiItem(
                ItemStack(Material.AZURE_BLUET).apply {itemMeta = itemMeta!!.apply {
                    setDisplayName(colorize("&rBurst"))
                    addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    if (fwEffect.type == FireworkEffect.Type.BURST) {
                        lore = colorizeList(
                            "&r&7SELECTED"
                        )
                        addEnchant(Enchantment.LUCK, 1, true)
                    }
                }}
            ) { event ->
                event.isCancelled = true

                fwEffect = fireworkManager
                    .editEffect(fireworkId, fwEffect) {
                        fwEffect.clone().with(FireworkEffect.Type.BURST)
                    }

                setAndUpdate()
            }, 3, 0
        )

        effectTypePane.addItem(
            GuiItem(
                ItemStack(Material.CREEPER_HEAD).apply {itemMeta = itemMeta!!.apply {
                    setDisplayName(colorize("&rCreeper Head"))
                    addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    if (fwEffect.type == FireworkEffect.Type.CREEPER) {
                        lore = colorizeList(
                            "&r&7SELECTED"
                        )
                        addEnchant(Enchantment.LUCK, 1, true)
                    }
                }}
            ) { event ->
                event.isCancelled = true

                fwEffect = fireworkManager
                    .editEffect(fireworkId, fwEffect) {
                        fwEffect.clone().with(FireworkEffect.Type.CREEPER)
                    }

                setAndUpdate()
            }, 4, 0
        )

        effectTypePane.addItem(
            GuiItem(
                ItemStack(Material.NETHER_STAR).apply {itemMeta = itemMeta!!.apply {
                    setDisplayName(colorize("&rStar"))
                    addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    if (fwEffect.type == FireworkEffect.Type.STAR) {
                        lore = colorizeList(
                            "&r&7SELECTED"
                        )
                        addEnchant(Enchantment.LUCK, 1, true)
                    }

                }}
            ) { event ->
                event.isCancelled = true

                fwEffect = fireworkManager
                    .editEffect(fireworkId, fwEffect) {
                        fwEffect.clone().with(FireworkEffect.Type.STAR)
                    }

                setAndUpdate()
            }, 5, 0
        )
    }

    override val canGoBack = true
    override fun back(event: InventoryClickEvent) {
        EditFireworkEffectMenu(fireworkManager, fireworkId, fwEffect).show(event.whoClicked)
    }
}