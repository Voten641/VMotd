package me.voten.vmotd

import com.comphenix.packetwrapper.WrapperStatusServerServerInfo
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.comphenix.protocol.wrappers.WrappedServerPing
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random


class VMotd : JavaPlugin() {

    companion object{
        lateinit var instance : VMotd
        var motdlist : ArrayList<String> = ArrayList<String>()
        var maxplayers : Int = 0
        var fakeplayers : Int = 0
        lateinit var protocolManager : ProtocolManager
        var serverdesc : ArrayList<WrappedGameProfile> = ArrayList()
    }

    fun InitVMotd(vm : VMotd ){
        instance = vm
    }

    override fun onEnable() {
        InitVMotd(this)
        saveDefaultConfig()
        config.options().copyDefaults(true)
        saveConfig()
        server.pluginManager.registerEvents(MotdChangeEvent, this)
        fakeplayers = config.getInt("fakePlayersAmount")
        for(s in config.getStringList("motd")){
            motdlist.add(s.replace("&","§"))
        }
        for(s in config.getStringList("serverDescription")){
            serverdesc.add(WrappedGameProfile(UUID.randomUUID(), s.replace("&","§")))
        }
        maxplayers = config.getInt("maxPlayers")

        protocolManager = ProtocolLibrary.getProtocolManager()
        protocolManager.addPacketListener(object : PacketAdapter(this, ListenerPriority.NORMAL,
            PacketType.Status.Server.SERVER_INFO) {
            override fun onPacketSending(event: PacketEvent) {
                handlePing(event.packet.serverPings.read(0))
            }
        })
    }

    private fun handlePing(ping: WrappedServerPing) {
        ping.setPlayers(
            serverdesc
        )
        if(config.getBoolean("fakePlayers")){
            ping.playersOnline = when(config.getBoolean("showRealPlayers")){
                true -> Bukkit.getOnlinePlayers().size+ fakeplayers
                false -> fakeplayers
            }
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
