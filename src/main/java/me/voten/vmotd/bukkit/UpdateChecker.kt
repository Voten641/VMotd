package me.voten.vmotd.bukkit

import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import org.bukkit.Bukkit
import java.net.URL
import java.util.*
import java.util.function.Consumer

// From: https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
class UpdateChecker {
    private val plugin: JavaPlugin = VMotd.instance
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
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            getVersion { version: String ->
                if (plugin.description.version == version) {
                    plugin.logger.info("There is not a new update available.")
                } else {
                    plugin.logger.info("There is a new update available.")
                }
            }
        }, 0, (20 * 60 * 60).toLong())
    }
}