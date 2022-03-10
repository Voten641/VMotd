package me.voten.vmotd.bungee

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.io.IOException
import org.bukkit.Bukkit
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

// From: https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
class UpdateChecker {
    private val plugin: Plugin = VMotd.instance
    private fun getVersion(consumer: Consumer<String>) {
        try {
            URL("https://api.spigotmc.org/legacy/update.php?resource=100560").openStream().use { inputStream ->
                Scanner(inputStream).use { scanner ->
                    if (scanner.hasNext()) {
                        consumer.accept(scanner.next())
                    }
                }
            }
        } catch (exception: IOException) {
            plugin.logger.info("Unable to check for updates: " + exception.message)
        }
    }

    fun checkVersion() {
        ProxyServer.getInstance().scheduler.schedule(plugin, Runnable {
            getVersion { version: String ->
                if (plugin.description.version == version) {
                    plugin.logger.info("There is not a new update available.")
                } else {
                    plugin.logger.info("There is a new update available.")
                }
            }
        }, 0, 1, TimeUnit.HOURS)
    }
}