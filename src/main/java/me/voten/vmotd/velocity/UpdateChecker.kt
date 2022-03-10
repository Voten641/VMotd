package me.voten.vmotd.velocity

import net.md_5.bungee.api.ProxyServer
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

// From: https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
class UpdateChecker {

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
            VMotd.logger.info("Unable to check for updates: " + exception.message)
        }
    }

    fun checkVersion() {
        VMotd.server.scheduler
            .buildTask(VMotd.instance) {
                getVersion { version: String ->
                    if (VMotd.server.pluginManager.fromInstance(VMotd.instance).get().description.version.equals(version)) {
                        VMotd.logger.info("There is not a new update available.")
                    } else {
                        VMotd.logger.info("There is a new update available.")
                    }
                }
            }
            .repeat(1L, TimeUnit.HOURS)
            .schedule()
    }
}