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

class EditFireworkMenu(
    private val fireworkManager: FireworkManager,
    private val fireworkId: String,
): MenuGui(3, "Edit Firework Effects") {
    private val effectsPane = StaticPane(1, 1, 6, 1)

    init {
        effectsPane.priority = Pane.Priority.HIGHEST
        addPane(effectsPane)
    }

    override fun setItems() {
        (0..effectsPane.size).forEach {
            effectsPane.addItem(GuiItem(ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
                itemMeta = itemMeta!!.apply {
                    setDisplayName(colorize("&r&7Click to add an effect"))
                }
            }) { event ->
                event.isCancelled = true
                if (!event.isLeftClick) return@GuiItem

                fireworkManager.addFirework(fireworkId, fireworkManager.fireworks()[fireworkId]!!.clone().apply {
                    addEffect(
                        FireworkEffect.builder()
                            .withColor(Color.WHITE)
                            .with(FireworkEffect.Type.BALL)
                            .build()
                    )
                })

                setAndUpdate()
            }, it, 0)
        }


        val powerButton = GuiItem(ItemStack(Material.TNT).apply {
            itemMeta = itemMeta!!.apply {
                setDisplayName(colorize("&rPower"))
                lore = colorizeList(
                    "&r&7Current Power: &e${fireworkManager.fireworks()[fireworkId]!!.power}",
                    "&r&7Click to &aadd",
                    "&r&7Right click to &csubtract"
                )
            }
        }) { event ->
            event.isCancelled = true

            val firework = fireworkManager.fireworks()[fireworkId]!!

            if (event.isLeftClick) {
                if (firework.power < 127) {
                    firework.power++
                }
            } else if (event.isRightClick) {
                if (firework.power > 0) {
                    firework.power--
                }
            }

            fireworkManager.addFirework(fireworkId, firework)

            event.currentItem = event.currentItem!!.apply {
                itemMeta = itemMeta!!.apply {
                    lore = colorizeList("&r&7${fireworkManager.fireworks()[fireworkId]!!.power + 1}")
                }
            }

            setAndUpdate()
        }

        basePane.addItem(powerButton, 7, 1)

        fireworkManager.fireworks()[fireworkId]!!.effects.forEachIndexed { index, effect ->
            effectsPane.addItem(
                GuiItem(
                    ItemStack(Material.NETHER_STAR).apply {
                        itemMeta = itemMeta!!.apply {
                            setDisplayName(colorize("&r&7Effect ${index + 1}"))
                            lore = colorizeList(
                                "&r&7Type: &e${effect.type.name.lowercase().replaceFirstChar { it.uppercase() }}",
                                "&r&7Colors: &e${effect.colors.joinToString(", ") {
                                    colorize("#" + it.toHexString() + it.toHexString())
                                }}",
                                "&r&7Fade Colors: &e${effect.fadeColors.joinToString(", ") {
                                    colorize("#" + it.toHexString() + it.toHexString())
                                }}",
                                "&r&7Flicker: &e${effect.hasFlicker()}",
                                "&r&7Trail: &e${effect.hasTrail()}",
                                "&r&cRight click to remove"
                            )
                        }
                    }
                ) {
                    it.isCancelled = true
                    if (it.isLeftClick) {
                        EditFireworkEffectMenu(fireworkManager, fireworkId, effect).show(it.whoClicked)
                    } else if (it.isRightClick) {
                        val firework = fireworkManager.fireworks()[fireworkId]!!
                        fireworkManager.addFirework(fireworkId, firework.clone().apply {
                            removeEffect(index)
                        })
                    }
                    setAndUpdate()
                }, index, 0
            )
        }
    }

    override val canGoBack = true
    override fun back(event: InventoryClickEvent) {
        FireworkyMenu(fireworkManager).show(event.whoClicked)
    }
}