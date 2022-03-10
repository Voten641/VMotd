package me.voten.vmotd.bungee

import net.md_5.bungee.api.Favicon
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.util.logging.Level
import javax.imageio.ImageIO


class VMotd : Plugin() {

    companion object{
        lateinit var instance : VMotd
        var motdlist : ArrayList<String> = ArrayList()
        var maxPlayers : Int = 0
        var fakePlayers : Int = 0
        var serverDesc : ArrayList<String> = ArrayList()
        var icons : ArrayList<Favicon> = ArrayList()
        lateinit var textAsPlayers : String
        lateinit var folder : File
        lateinit var conf : Configuration
    }

    override fun onEnable(){
        instance = this
        ProxyServer.getInstance().pluginManager.registerListener(this, PingEvent)
        if (!dataFolder.exists()) dataFolder.mkdir()
        val file = File(dataFolder, "config.yml")
        if (!file.exists()) {
            try {
                getResourceAsStream("config.yml").use { `in` -> Files.copy(`in`, file.toPath()) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        folder = File(dataFolder, "/icons")
        if(!folder.exists()){
            if(!folder.mkdirs()) return logger.log(Level.WARNING, "Error while creating Icons folder")
        }
        if(folder.listFiles() != null) {
            for (icon in folder.listFiles()) {
                if (isPng(icon)) {
                    icons.add(Favicon.create(ImageIO.read(icon)))
                }
            }
        }
        conf = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(file)
        fakePlayers = conf.getInt("fakePlayersAmount")
        textAsPlayers = conf.getString("textAsPlayerCount").replace("&","§")
        for(s in conf.getStringList("motd")){
            motdlist.add(s.replace("&","§"))
        }
        for(s in conf.getStringList("serverDescription")){
            serverDesc.add(s.replace("&","§"))
        }
        maxPlayers = conf.getInt("maxPlayers")
        ProxyServer.getInstance().pluginManager.registerCommand(this, VMotdCommand)
        UpdateChecker().checkVersion()
    }

    @Throws(IOException::class)
    public fun isPng(file: File): Boolean {
        FileInputStream(file).use { `is` -> return `is`.read() == 137 }
    }
}