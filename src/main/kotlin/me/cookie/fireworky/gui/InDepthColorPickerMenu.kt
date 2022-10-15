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

class InDepthColorPickerMenu(
    private val fireworkManager: FireworkManager,
    private val fireworkId: String,
    fireworkEffect: FireworkEffect,
    private val editingColor: Color,
    private val pickedColor: Color,
    private val editingColorType: EditingColor
): MenuGui(6, "Select a color", fireworkManager, fireworkId) {
    private var fwEffect = fireworkEffect
    private val colorsPane = StaticPane(1, 1, 7, 4)

    private val white = if(pickedColor == Color.WHITE) 0 else 1

    init {
        colorsPane.priority = Pane.Priority.HIGHEST

        addPane(colorsPane)
    }

    override fun setItems() {
        colorsPane.fillWith(filler(Material.GRAY_STAINED_GLASS_PANE).item) { event -> event.isCancelled = true }

        (0..28).forEach {
            colorsPane.addItem(
                GuiItem(ItemStack(Material.LEATHER_CHESTPLATE).apply { itemMeta = (itemMeta!! as LeatherArmorMeta).apply {
                    val newColor = pickedColor
                        .darken(
                            (toXY(it, 7).second.toDouble() * (0.20 * if(white == 0) 1.35f else 1f))
                                .coerceAtMost(1.0),
                            // woo magic numbers numbers!!!
                        ).saturation(
                            ((toXY(it, 7).first + 1).toDouble() * 0.14) * white
                        )
                    setDisplayName(colorize("&7${newColor.toHexString()}"))
                    setColor(newColor)
                }}) { event ->
                    event.isCancelled = true

                    val color = Color.fromRGB((event.currentItem!!.itemMeta!! as LeatherArmorMeta).color.asRGB())

                    fwEffect = fireworkManager.editEffect(fireworkId, fwEffect) {
                        if (editingColorType == EditingColor.PRIMARY) {
                            fwEffect.clone(noColor = true).withColor(
                                *fwEffect.colors.toMutableList().apply {
                                    var found = false
                                    replaceAll { fwColor ->
                                        if(fwColor == editingColor && !found) { found = true; color; } else fwColor
                                    }
                                }.toTypedArray()
                            )
                        } else {
                            fwEffect.clone(noFadeColor = true).withFade(
                                *fwEffect.fadeColors.toMutableList().apply {
                                    var found = false
                                    replaceAll { fwColor ->
                                        if(fwColor == editingColor && !found) { found = true; color; } else fwColor
                                    }
                                }.toTypedArray()
                            )
                        }
                    }

                    EditFireworkEffectColorsMenu(fireworkManager, fireworkId, fwEffect, editingColorType)
                        .show(event.whoClicked)

                }, toXY(it, 7).first, toXY(it, 7).second
            )
        }
    }

    override val canGoBack = true
    override fun back(event: InventoryClickEvent) {
        BaseColorPickerMenu(fireworkManager, fireworkId, fwEffect, editingColor, editingColorType)
            .show(event.whoClicked)
    }
}