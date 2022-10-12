package me.cookie.fireworky.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.cookie.fireworky.*
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import kotlin.random.Random

class EditFireworkEffectColorsMenu(
    private val fireworkManager: FireworkManager,
    private val fireworkId: String,
    fireworkEffect: FireworkEffect,
    private val editingColorType: EditingColor
): MenuGui(3, "Edit Firework Effect Colors") {
    private val effectsPane = StaticPane(1, 1, 7, 1)
    private var fwEffect = fireworkEffect

    init {
        effectsPane.priority = Pane.Priority.HIGHEST
        addPane(effectsPane)
    }

    override fun setItems() {
        basePane.addItem(
            GuiItem(ItemStack(Material.ARROW).apply { itemMeta = itemMeta!!.apply {
                setDisplayName(colorize("&r&7Back"))
            }}
            ) { event ->
                event.isCancelled = true

                EditFireworkEffectMenu(fireworkManager, fireworkId, fwEffect).show(event.whoClicked)
            }, 7, 0
        )

        basePane.addItem(
            GuiItem(ItemStack(Material.RED_STAINED_GLASS_PANE).apply { itemMeta = itemMeta!!.apply {
                setDisplayName(colorize("&r&cClose"))
            }}
            ) { event ->
                event.isCancelled = true

                event.whoClicked.closeInventory()
            }, 8, 0
        )

        (0..effectsPane.size).forEach {
            effectsPane.addItem(GuiItem(ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
                itemMeta = itemMeta!!.apply {
                    setDisplayName(colorize("&r&7Click to add a color"))
                }
            }) { event ->
                event.isCancelled = true
                if (!event.isLeftClick) return@GuiItem

                fwEffect = fireworkManager.editEffect(fireworkId, fwEffect) {
                    if (editingColorType == EditingColor.PRIMARY) {
                        fwEffect.clone(noColor = true).withColor(
                            *fwEffect.colors.toTypedArray(),
                            Color.fromRGB(
                                Random.nextInt(255),
                                Random.nextInt(255),
                                Random.nextInt(255)
                            )
                        )
                    } else {
                        fwEffect.clone(noFadeColor = true).withFade(
                            *fwEffect.fadeColors.toTypedArray(),
                            Color.fromRGB(
                                Random.nextInt(255),
                                Random.nextInt(255),
                                Random.nextInt(255)
                            )
                        )
                    }
                }

                setAndUpdate()
            }, it, 0)
        }

        val loopThrough = if (editingColorType == EditingColor.PRIMARY)
            fwEffect.colors
        else fwEffect.fadeColors

        loopThrough.forEachIndexed { index, color ->
            effectsPane.addItem(
                GuiItem(ItemStack(Material.LEATHER_CHESTPLATE).apply {
                    itemMeta = (itemMeta!! as LeatherArmorMeta).apply {
                        setDisplayName(colorize("&r&7${color.asHex()}"))
                        setColor(color)
                    }
                }) { event ->
                    event.isCancelled = true

                    if (event.isLeftClick) {
                        BaseColorPickerMenu(fireworkManager, fireworkId, fwEffect, color, editingColorType).show(event.whoClicked)
                    } else if (event.isRightClick) {
                        fwEffect = fireworkManager.editEffect(fireworkId, fwEffect) {
                            if (editingColorType == EditingColor.PRIMARY) {
                                if (fwEffect.colors.size == 1) {
                                    event.whoClicked.sendMessage(
                                        colorize("&r&cYou can't remove the last color!")
                                    )
                                    return@editEffect fwEffect.clone()
                                }
                                fwEffect.clone(noColor = true).withColor(
                                    *fwEffect.colors.filter {
                                        it != color && fwEffect.colors.size > 1
                                    }.toTypedArray()
                                )
                            } else {
                                if (fwEffect.fadeColors.size == 1) {
                                    event.whoClicked.sendMessage(
                                        colorize("&r&cYou can't remove the last color!")
                                    )
                                    return@editEffect fwEffect.clone()
                                }
                                fwEffect.clone(noFadeColor = true).withFade(
                                    *fwEffect.fadeColors.filter {
                                        it != color && fwEffect.fadeColors.size > 1
                                    }.toTypedArray()
                                )
                            }
                        }
                    }

                    setAndUpdate()
                }, index, 0
            )
        }
    }

    override val canGoBack = true
    override fun back(event: InventoryClickEvent) {
        EditFireworkEffectMenu(fireworkManager, fireworkId, fwEffect).show(event.whoClicked)
    }
}