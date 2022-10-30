package me.cookie.fireworky.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.cookie.fireworky.EditingColor
import me.cookie.fireworky.FireworkManager
import me.cookie.fireworky.Fireworky
import me.cookie.fireworky.clone
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class HexColorPickerMenu(private val fireworkManager: FireworkManager,
                         private val editingFirework: String,
                         fireworkEffect: FireworkEffect,
                         private val editingColor: Color,
                         private val editingColorType: EditingColor
): AnvilGui("Enter a hex color") {
    private var fwEffect = fireworkEffect
    private val first = StaticPane(0, 0, 1, 1)
    private val second = StaticPane(0, 0, 1, 1)
    private val third = StaticPane(0, 0, 1, 1)

    private val renamer = object: BukkitRunnable() {
        override fun run() {
            val color = try {
                Color.fromRGB(Integer.parseInt(renameText, 16))
            } catch (_: NumberFormatException) { Color.BLACK }

            third.addItem(GuiItem(ItemStack(Material.LEATHER_CHESTPLATE).apply {
                itemMeta = (itemMeta as LeatherArmorMeta).apply {
                    setColor(color)
                }
            }) { event ->
                event.isCancelled = true
                fwEffect = fireworkManager.editEffect(editingFirework, fwEffect) {
                    if (editingColorType == EditingColor.PRIMARY) {
                        fwEffect.clone(noColor = true).withColor(
                            *fwEffect.colors.toMutableList().apply {
                                var found = false
                                replaceAll { fwColor ->
                                    if (fwColor == editingColor && !found) { found = true; color; } else fwColor
                                }
                            }.toTypedArray()
                        )
                    } else {
                        fwEffect.clone(noFadeColor = true).withFade(
                            *fwEffect.fadeColors.toMutableList().apply {
                                var found = false
                                replaceAll { fwColor ->
                                    if (fwColor == editingColor && !found) { found = true; color; } else fwColor
                                }
                            }.toTypedArray()
                        )
                    }
                }

                EditFireworkEffectColorsMenu(fireworkManager, editingFirework, fwEffect, editingColorType)
                    .show(event.whoClicked)
            }, 0, 0)
            first.addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE).apply { itemMeta =
                itemMeta!!.apply { setDisplayName(renameText) }
            }), 0, 0)

            update()
        }
    }

    init {
        first.addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)) {
            it.isCancelled = true
        }, 0, 0)
        second.addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)) {
            it.isCancelled = true
        }, 0, 0)

        firstItemComponent.addPane(first)
        secondItemComponent.addPane(second)
        resultComponent.addPane(third)

        update()
        startRenamer()
    }


    private fun startRenamer() {
        renamer.runTaskTimer(JavaPlugin.getPlugin(Fireworky::class.java), 0, 60)
    }

    private fun stopRenamer() {
        renamer.cancel()
    }

    override fun callOnClose(event: InventoryCloseEvent) {
        stopRenamer()
    }
}