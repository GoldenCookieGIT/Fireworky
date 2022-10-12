package me.cookie.fireworky.serializers

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import me.cookie.fireworky.FireworkManager
import me.cookie.fireworky.gson
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.inventory.meta.FireworkMeta
import java.lang.reflect.Type

class FireworkMetaSerializer(
    private val fireworkManager: FireworkManager,
): JsonSerializer<FireworkMeta>, JsonDeserializer<FireworkMeta> {
    override fun serialize(
        src: FireworkMeta,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val result = JsonObject()
        result.add("fireworkEffects", gson!!.toJsonTree(src.effects, object : TypeToken<List<FireworkEffect>>() {}.type))
        result.add("power", JsonPrimitive(src.power))
        return result
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): FireworkMeta {
        val fwEffects = json.asJsonObject.getAsJsonArray("fireworkEffects").map {
            val colors = it.asJsonObject.getAsJsonArray("colors").map { color ->
                Color.fromRGB(
                    color.asJsonObject.get("red").asInt.unSign(),
                    color.asJsonObject.get("green").asInt.unSign(),
                    color.asJsonObject.get("blue").asInt.unSign()
                )
            }

            val fadeColors = it.asJsonObject.getAsJsonArray("fadeColors").map { color ->
                Color.fromRGB(
                    color.asJsonObject.get("red").asInt.unSign(),
                    color.asJsonObject.get("green").asInt.unSign(),
                    color.asJsonObject.get("blue").asInt.unSign()
                )
            }

            val flicker = it.asJsonObject.get("flicker").asBoolean
            val trail = it.asJsonObject.get("trail").asBoolean

            val type = FireworkEffect.Type.valueOf(it.asJsonObject.get("type").asString)

            FireworkEffect.builder()
                .with(type)
                .withColor(colors)
                .withFade(fadeColors)
                .flicker(flicker)
                .trail(trail)
                .build()
        }

        val fwPower = json.asJsonObject.getAsJsonPrimitive("power").asInt

        return fireworkManager.dummyFireworkMeta.clone()
            .apply {
                power = fwPower
                addEffects(fwEffects)
            }
    }

    private fun Int.unSign(): Int {
        return this and 0xff
    }
}