package me.cookie.fireworky.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.cookie.fireworky.*
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

abstract class MenuGui(
    rows: Int,
    name: String,
    private val fireworkManager: FireworkManager,
    private val editingFirework: String,
): ChestGui(rows, name) {
    val basePane = StaticPane(0, 0, 9, rows)

    init {
        addPane(basePane)
    }

    override fun show(humanEntity: HumanEntity) {
        super.show(humanEntity)
        if (!isUpdating) setAndUpdate()
    }

    fun setAndUpdate() {
        (0..basePane.size).forEach {
            basePane.addItem(filler(Material.BLACK_STAINED_GLASS_PANE), toXY(it, basePane.length).first,
                toXY(it, basePane.length).second)
        }

        if (canGoBack) {
            basePane.addItem(
                GuiItem(ItemStack(Material.ARROW).apply { itemMeta = itemMeta!!.apply {
                    setDisplayName(colorize("&r&7Back"))
                }}
                ) { event ->
                    event.isCancelled = true
                    back(event)
                }, 7, 0
            )
        }

        basePane.addItem(
            GuiItem(ItemStack(Material.RED_STAINED_GLASS_PANE).apply { itemMeta = itemMeta!!.apply {
                setDisplayName(colorize("&r&cClose"))
            }}) { event ->
                event.isCancelled = true

                event.whoClicked.closeInventory()
            }, 8, 0
        )

        if (editingFirework.isNotEmpty()) {
            basePane.addItem(
                GuiItem(ItemStack(Material.FIREWORK_ROCKET).apply { itemMeta = itemMeta!!.apply {
                    setDisplayName(colorize("&eEditing: &r&7$editingFirework"))
                }}) { event ->
                    event.isCancelled = true
                    EditFireworkMenu(fireworkManager, editingFirework).show(event.whoClicked)
                }, 6, 0
            )
        }

        setItems()

        update()
    }

    protected abstract fun setItems()

    abstract val canGoBack: Boolean
    abstract fun back(event: InventoryClickEvent)
}