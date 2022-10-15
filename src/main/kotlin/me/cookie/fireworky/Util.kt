package me.cookie.fireworky

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.google.gson.Gson
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun colorizeList(vararg strings: String): List<String> {
    return strings.clone().asList().map { colorize(it) }
}

fun colorize(string: String): String {
    return ChatColor.translateAlternateColorCodes('&', string)
}

val Pane.size: Int
    get() = length * height

fun filler(type: Material): GuiItem {
    return GuiItem(ItemStack(type).apply {
        itemMeta = itemMeta!!.apply {
            setDisplayName(" ")
        }
    }) { it.isCancelled = true }
}

fun toXY(index: Int, width: Int): Pair<Int, Int> {
    return Pair(index % width, index / width)
}

fun FireworkEffect.clone(noType: Boolean = false,
                         noColor: Boolean = false,
                         noFadeColor: Boolean = false,
                         noFlicker: Boolean = false,
                         noTrail: Boolean = false): FireworkEffect.Builder {
    return FireworkEffect.builder()
            .with(if (noType) FireworkEffect.Type.BALL else this.type)
            .withColor(if (noColor) listOf() else this.colors)
            .withFade(if (noFadeColor) listOf() else this.fadeColors)
            .flicker(if (noFlicker) false else this.hasFlicker())
            .trail(if (noTrail) false else this.hasTrail())
}

fun Color.shiftHue(hueShift: Int): Color {
    val hsb = java.awt.Color.RGBtoHSB(this.red, this.green, this.blue, null)
    val hue = (hsb[0] + hueShift / 360.0).toFloat()
    val javaColor = java.awt.Color.getHSBColor(hue, hsb[1], hsb[2])
    return Color.fromRGB(javaColor.red, javaColor.green, javaColor.blue)
}

fun Color.toHexString(): String {
    return String.format("%02x%02x%02x", red, green, blue)
}

fun Color.darken(percent: Double): Color {
    require(!(percent < 0 || percent > 1.00)) { "Percentage must be between [0.00 - 1.00]" }
    var r: Int = this.red
    var g: Int = this.green
    var b: Int = this.blue

    r = (r * (1.00 - percent)).toInt()
    g = (g * (1.00 - percent)).toInt()
    b = (b * (1.00 - percent)).toInt()
    return Color.fromRGB(r, g, b)
}

fun Color.saturation(percent: Double): Color {
    val hsb = java.awt.Color.RGBtoHSB(red, green, blue, null)
    val javaColor = java.awt.Color.getHSBColor(hsb[0], percent.toFloat(), hsb[2])
    return Color.fromRGB(javaColor.red, javaColor.green, javaColor.blue)
}

var gson: Gson? = null