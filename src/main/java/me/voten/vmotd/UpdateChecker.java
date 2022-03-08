package me.voten.vmotd;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

// From: https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
public class UpdateChecker {

    private final JavaPlugin plugin = VMotd.getPlugin(VMotd.class);

    public void getVersion(final Consumer<String> consumer) {
        int resourceId = 100560;
        try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
            if (scanner.hasNext()) {
                consumer.accept(scanner.next());
            }
        } catch (IOException exception) {
            plugin.getLogger().info("Unable to check for updates: " + exception.getMessage());
        }
    }

    public void checkVersion(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            getVersion(version -> {
                if (plugin.getDescription().getVersion().equals(version)) {
                    plugin.getLogger().info("There is not a new update available.");
                } else {
                    plugin.getLogger().info("There is a new update available.");
                }
            });
        },0, 20*60*60);
    }
}