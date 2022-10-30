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

class BaseColorPickerMenu(
    private val fireworkManager: FireworkManager,
    private val fireworkId: String,
    fireworkEffect: FireworkEffect,
    private val editingColor: Color,
    private val editingColorType: EditingColor
): MenuGui(5, "Select a color", fireworkManager, fireworkId) {
    private var fwEffect = fireworkEffect
    private val colorsPane = StaticPane(1, 1, 7, 3, Pane.Priority.HIGHEST)

    init {
        addPane(colorsPane)
    }

    override fun setItems() {
        colorsPane.fillWith(filler(Material.GRAY_STAINED_GLASS_PANE).item) { event -> event.isCancelled = true }

        val colors = colorsPane.size

        (0..colors).forEach {
            var color = Color.RED.shiftHue(360*(it)/colors)
            if (it == colors-1) {
                color = Color.fromRGB(0xffffff)
            }

            colorsPane.addItem(
                GuiItem(ItemStack(Material.LEATHER_CHESTPLATE).apply {
                    itemMeta = (itemMeta!! as LeatherArmorMeta).apply {
                        setDisplayName(colorize("&7${color.toHexString()}"))
                        setColor(color)
                    }
                }) { event ->
                    event.isCancelled = true
                    InDepthColorPickerMenu(fireworkManager, fireworkId, fwEffect, editingColor, color, editingColorType)
                        .show(event.whoClicked)
                }, toXY(it, 7).first, toXY(it, 7).second
            )
            basePane.addItem(GuiItem(ItemStack(Material.LIME_DYE).apply {
                itemMeta = itemMeta!!.apply {
                    setDisplayName(colorize("&a&lHex Color Picker"))
                }
            }) { event ->
                event.isCancelled = true
                HexColorPickerMenu(fireworkManager, fireworkId, fwEffect, editingColor, editingColorType)
                    .show(event.whoClicked)
            }, 4, 4)
        }
    }

    override val canGoBack = true
    override fun back(event: InventoryClickEvent) {
        EditFireworkEffectColorsMenu(fireworkManager, fireworkId, fwEffect, editingColorType).show(event.whoClicked)
    }
}