package me.voten.vmotd.bungee

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.ServerPing
import net.md_5.bungee.api.ServerPing.PlayerInfo
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.util.*
import java.util.regex.Pattern
import kotlin.random.Random


object PingEvent : Listener {

    @EventHandler
    fun onPing(e: ProxyPingEvent){
        val sinfo : ServerPing? = e.response
        val sample = arrayOfNulls<PlayerInfo>(VMotd.serverDesc.size)
        for(i in 0 until VMotd.serverDesc.size){
            sample[i] = PlayerInfo(setString(VMotd.serverDesc[i]), UUID.randomUUID())
        }
        if(VMotd.textAsPlayerbol) {
            sinfo?.version = ServerPing.Protocol(setString(VMotd.textAsPlayers[Random.nextInt(0, VMotd.textAsPlayers.size)]), sinfo?.version?.protocol!!-5)
        }
        if(VMotd.icons.size > 0) sinfo?.setFavicon(VMotd.icons[Random.nextInt(0, VMotd.icons.size)])
        sinfo?.players?.max = VMotd.maxPlayers
        sinfo?.players?.sample = sample
        sinfo?.players?.online = getPlayers()
        sinfo?.descriptionComponent = TextComponent(setString(VMotd.motdlist[Random.nextInt(0, VMotd.motdlist.size)]))
        e.response = sinfo
    }

    private fun setString(s : String) : String{
        return s.replace("%playeronline%", getPlayers().toString())
            .replace("%newline%", "\n")
            .replace("%playermax%", VMotd.maxPlayers.toString())
            .replace("&","§")
    }

    private fun getPlayers() : Int{
        if(VMotd.fakePlayersbol){
            val playersOnline = when(VMotd.showRealPlayers){
                true -> ProxyServer.getInstance().onlineCount + VMotd.fakePlayers
                false -> VMotd.fakePlayers
            }
            return playersOnline
        }
        return ProxyServer.getInstance().onlineCount
    }
}