package me.voten.vmotd.bungee

import net.md_5.bungee.api.Favicon
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.util.logging.Level
import javax.imageio.ImageIO


class VMotd : Plugin() {

    companion object{
        var textAsPlayerbol: Boolean = true
        var fakePlayersbol: Boolean = true
        var showRealPlayers: Boolean = true
        lateinit var instance : VMotd
        var motdlist : ArrayList<String> = ArrayList()
        var maxPlayers : Int = 0
        var fakePlayers : Int = 0
        var serverDesc : ArrayList<String> = ArrayList()
        var icons : ArrayList<Favicon> = ArrayList()
        var textAsPlayers : ArrayList<String> = ArrayList()
        lateinit var folder : File
        lateinit var conf : Configuration
    }

    override fun onEnable(){
        instance = this
        ProxyServer.getInstance().pluginManager.registerListener(this, PingEvent)
        ProxyServer.getInstance().pluginManager.registerCommand(this, VMotdCommand)
        UpdateChecker().checkVersion()
        loadConfig()
    }

    fun loadConfig(){
        motdlist.clear()
        textAsPlayers.clear()
        serverDesc.clear()
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
                    val img : BufferedImage = ImageIO.read(icon)
                    if(img.width == 64 && img.height ==64){
                        icons.add(Favicon.create(img))
                    }else println("Icon " + icon.name + " cant be loaded, icon must be 64x64 pixels")
                }
            }
        }
        conf = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(file)
        fakePlayers = conf.getInt("fakePlayersAmount")
        for(s in conf.getStringList("textAsPlayerCount")){
            textAsPlayers.add(s.replace("&","§"))
        }
        for(s in conf.getStringList("motd")){
            motdlist.add(s.replace("&","§"))
        }
        for(s in conf.getStringList("serverDescription")){
            serverDesc.add(s.replace("&","§"))
        }
        maxPlayers = conf.getInt("maxPlayers")
        showRealPlayers = conf.getBoolean("showRealPlayers")
        fakePlayersbol = conf.getBoolean("fakePlayers")
        textAsPlayerbol = conf.getBoolean("enableTextAsPlayerCount")
    }

    @Throws(IOException::class)
    public fun isPng(file: File): Boolean {
        FileInputStream(file).use { `is` -> return `is`.read() == 137 }
    }
}