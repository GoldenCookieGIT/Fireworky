package me.cookie.fireworky.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.cookie.fireworky.*
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class EditFireworkEffectMenu(
    private val fireworkManager: FireworkManager,
    private val fireworkId: String,
    fireworkEffect: FireworkEffect,
): MenuGui(3, "Edit Firework Effect", fireworkManager, fireworkId) {
    private val effectPane = StaticPane(1, 1, 6, 1)

    private var fwEffect = fireworkEffect

    init {
        effectPane.priority = Pane.Priority.HIGHEST
        addPane(effectPane)
    }

    override fun setItems() {
        effectPane.fillWith(filler(Material.GRAY_STAINED_GLASS_PANE).item) { event -> event.isCancelled = true }

        effectPane.addItem(
            GuiItem(
                ItemStack(Material.RED_DYE).apply { itemMeta = itemMeta!!.apply {
                    setDisplayName(colorize("&rEffect Colors"))
                    lore = colorizeList(
                        "&r&7Current Colors:",
                        *fwEffect.colors.map {
                            "&r&7- ${colorize("#" + it.toHexString() + it.toHexString())}"
                        }.toTypedArray()
                    )
                }}
            ) { event ->
                event.isCancelled = true

                EditFireworkEffectColorsMenu(fireworkManager, fireworkId, fwEffect, EditingColor.PRIMARY)
                    .show(event.whoClicked)
            },
            1, 0
        )

        effectPane.addItem(
            GuiItem(
                ItemStack(Material.BLUE_DYE).apply {
                    itemMeta = itemMeta!!.apply {
                        setDisplayName(colorize("&rFade Colors"))
                        lore = colorizeList(
                            "&r&7Current Fade Colors:",
                            *fwEffect.fadeColors.map {
                                "&r&7- ${colorize("#" + it.toHexString() + it.toHexString())}"
                            }.toTypedArray()
                        )
                    }
                }
            ) { event ->
                event.isCancelled = true

                EditFireworkEffectColorsMenu(fireworkManager, fireworkId, fwEffect, EditingColor.FADE)
                    .show(event.whoClicked)
            },
            2, 0
        )

        effectPane.addItem(
            GuiItem(
                ItemStack(Material.FIREWORK_STAR).apply {
                    itemMeta = itemMeta!!.apply {
                        setDisplayName(colorize("&rEffect Type"))
                        lore = colorizeList(
                            "&r",
                            "&r&7Current: &e${
                                if (fwEffect.type.name.split("_").size > 1) {
                                    val splitEffect = fwEffect.type.name.split("_")
                                    splitEffect[1].lowercase().replaceFirstChar { it.uppercase() } + " " + splitEffect[0]
                                        .lowercase().replaceFirstChar { it.uppercase() }
                                } else {
                                    fwEffect.type.name.lowercase().replaceFirstChar { it.uppercase() }
                                }
                            }",
                            "&r",
                            "&r&7Click to change"
                        )
                    }
                }
            ) { event ->
                event.isCancelled = true
                EditEffectTypeMenu(fireworkManager, fireworkId, fwEffect).show(event.whoClicked)
            },
            3, 0
        )

        effectPane.addItem(
            GuiItem(
                ItemStack(Material.END_ROD).apply {
                    itemMeta = itemMeta!!.apply {
                        setDisplayName(colorize("&rTrail"))
                        val trailText = if (fwEffect.hasTrail()) "&aENABLED" else "&cDISABLED"
                        lore = colorizeList("&r&l$trailText")
                    }
                }
            ) { event ->
                event.isCancelled = true

                fwEffect = fireworkManager
                    .editEffect(fireworkId, fwEffect) {
                        fwEffect.clone().trail(!fwEffect.hasTrail())
                    }

                setAndUpdate()
            },
            4, 0
        )

        effectPane.addItem(
            GuiItem(
                ItemStack(Material.GUNPOWDER).apply {
                    itemMeta = itemMeta!!.apply {
                        setDisplayName(colorize("&rFlicker"))
                        val flickerText = if (fwEffect.hasFlicker()) "&aENABLED" else "&cDISABLED"
                        lore = colorizeList("&r&l$flickerText")
                    }
                }
            ) { event ->
                event.isCancelled = true

                fwEffect = fireworkManager
                    .editEffect(fireworkId, fwEffect) {
                        fwEffect.clone().flicker(!fwEffect.hasFlicker())
                    }

                setAndUpdate()
            },
            5, 0
        )
    }

    override val canGoBack = true
    override fun back(event: InventoryClickEvent) {
        EditFireworkMenu(fireworkManager, fireworkId).show(event.whoClicked)
    }
}