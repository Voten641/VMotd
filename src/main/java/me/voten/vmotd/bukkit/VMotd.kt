package me.voten.vmotd.bukkit

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.comphenix.protocol.wrappers.WrappedServerPing
import net.md_5.bungee.api.Favicon
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import java.util.logging.Level
import javax.imageio.ImageIO
import kotlin.random.Random


class VMotd : JavaPlugin() {

    companion object{
        lateinit var instance : VMotd
        var motdlist : ArrayList<String> = ArrayList()
        var maxplayers : Int = 0
        var fakeplayers : Int = 0
        lateinit var protocolManager : ProtocolManager
        var serverdesc : ArrayList<WrappedGameProfile> = ArrayList()
        var icons : ArrayList<WrappedServerPing.CompressedImage> = ArrayList()
        var textasplayers : ArrayList<String> = ArrayList()
        lateinit var folder : File
        lateinit var conf : FileConfiguration
        var textasplayerbol : Boolean = true
        var fakeplayersbol : Boolean = true
        var showrealplayers : Boolean = true
    }

    fun InitVMotd(vm : VMotd){
        instance = vm
    }

    override fun onEnable() {
        InitVMotd(this)
        saveDefaultConfig()
        config.options().copyDefaults(true)
        config.options().header("avaible variables: %playeronline% %playermax% %serverversion%")
        saveConfig()
        conf = config
        loadConfig()
        getCommand("vmotd").executor = VMotdCommand
        protocolManager = ProtocolLibrary.getProtocolManager()
        protocolManager.addPacketListener(object : PacketAdapter(this, ListenerPriority.NORMAL,
            PacketType.Status.Server.SERVER_INFO) {
            override fun onPacketSending(event: PacketEvent) {
                handlePing(event.packet.serverPings.read(0))
            }
        })
        UpdateChecker().checkVersion()
    }

    fun loadConfig(){
        motdlist.clear()
        textasplayers.clear()
        serverdesc.clear()
        folder = File(dataFolder, "/icons")
        if(!folder.exists()){
            if(!folder.mkdirs()) return logger.log(Level.WARNING, "Error while creating Icons folder")
        }
        if(folder.listFiles() != null) {
            for (icon in folder.listFiles()) {
                if (isPng(icon)) {
                    val img : BufferedImage = ImageIO.read(icon)
                    if(img.width == 64 && img.height ==64){
                        icons.add(WrappedServerPing.CompressedImage.fromPng(FileInputStream(icon)))
                    }else println("Icon " + icon.name + " cant be loaded, icon must be 64x64 pixels")
                }
            }
        }
        fakeplayers = config.getInt("fakePlayersAmount")
        for (s in config.getStringList("textAsPlayerCount")) {
            textasplayers.add(s.replace("&","§"))
        }
        for(s in config.getStringList("motd")){
            motdlist.add(s.replace("&","§"))
        }
        for(s in config.getStringList("serverDescription")){
            serverdesc.add(WrappedGameProfile(UUID.randomUUID(), s.replace("&","§")))
        }
        maxplayers = config.getInt("maxPlayers")
        textasplayerbol = config.getBoolean("enableTextAsPlayerCount")
        fakeplayersbol = config.getBoolean("fakePlayers")
        showrealplayers = config.getBoolean("showRealPlayers")
    }

    private fun setString(s : String) : String{
        return s.replace("%playeronline%",getPlayers().toString())
            .replace("%newline%", "\n")
            .replace("%playermax%", maxplayers.toString())
            .replace("&","§")
            .replace("%serverversion%", server.bukkitVersion.substring(0, server.bukkitVersion.indexOf("-")))
    }

    @Throws(IOException::class)
    public fun isPng(file: File): Boolean {
        FileInputStream(file).use { `is` -> return `is`.read() == 137 }
    }
    private fun handlePing(ping: WrappedServerPing) {
        val serverdesc2 : ArrayList<WrappedGameProfile> = ArrayList()
        for(w in serverdesc){
            serverdesc2.add(WrappedGameProfile(w.uuid, setString(w.name)))
        }
        ping.setPlayers(serverdesc2)
        if(textasplayerbol) {
            ping.versionProtocol = -1
            ping.versionName = setString(textasplayers[Random.nextInt(0, textasplayers.size)])
        }
        ping.motD = WrappedChatComponent.fromText(setString(motdlist[Random.nextInt(0, motdlist.size)]))
        ping.playersMaximum = maxplayers
        if(icons.size > 0) ping.favicon = icons[Random.nextInt(0, icons.size)]
        ping.playersOnline = getPlayers()
    }

    private fun getPlayers() : Int{
        if(fakeplayersbol){
            val playersOnline = when(showrealplayers){
                true -> Bukkit.getOnlinePlayers().size+ fakeplayers
                false -> fakeplayers
            }
            return playersOnline
        }
        return Bukkit.getOnlinePlayers().size
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
