package me.voten.vmotd

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent
import kotlin.random.Random

object MotdChangeEvent : Listener {

    @EventHandler
    fun onServerPing(e : ServerListPingEvent){
        e.maxPlayers = VMotd.maxplayers
        e.motd = VMotd.motdlist[Random.nextInt(0, VMotd.motdlist.size)]
    }

}
