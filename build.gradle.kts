plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

val ver = "1.2.2"

group = "me.cookie"
version = ver

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.14")
    implementation("org.bstats:bstats-bukkit:3.0.0")
}

tasks {
    shadowJar {
        relocate("co.aikar.commands", "me.cookie.fireworky.acf")
        relocate("co.aikar.locales", "me.cookie.fireworky.locales")
        relocate("org.bstats", "me.cookie.fireworky.bstats")
        relocate("com.github.stefvanschie.inventoryframework", "me.cookie.fireworky.inventoryframework")
        destinationDirectory.set(file("server/plugins"))
    }
    compileJava {
        options.compilerArgs.add("-parameters")
        options.isFork = true
        options.forkOptions.executable = "javac"
    }
    compileKotlin {
        kotlinOptions.javaParameters = true
    }
}

bukkit {
    name = "Fireworky"
    version = ver
    main = "me.cookie.fireworky.Fireworky"
    author = "Cookie"
    apiVersion = "1.16" // Due to the use of the hex colors.
    libraries = listOf(
        "org.jetbrains.kotlin:kotlin-stdlib:1.7.10"
    )
}