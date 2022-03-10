package me.voten.vmotd.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyPingEvent
import com.velocitypowered.api.proxy.server.ServerPing
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.chat.TextComponent
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import kotlin.random.nextInt

object PingEvent {

    @Subscribe
    fun onPingEvent(e: ProxyPingEvent){
        val sp : ServerPing.Builder? = e.ping.asBuilder()
        if(VMotd.conf.getBoolean("fakePlayers"))
            sp?.onlinePlayers(getPlayers())
        val samples : ArrayList<ServerPing.SamplePlayer> = ArrayList()
        for(i in 0 until  VMotd.serverDesc.size){
            samples += ServerPing.SamplePlayer(setString(VMotd.serverDesc[i]), UUID.randomUUID())
        }
        if(VMotd.conf.getBoolean("enableTextAsPlayerCount"))
            sp?.version(ServerPing.Version(sp.version.protocol-5, setString(VMotd.textAsPlayers)))
        if(VMotd.icons.size > 0)
            sp?.favicon(VMotd.icons[Random.nextInt(0, VMotd.icons.size)])
        sp?.maximumPlayers(VMotd.maxPlayers)
        sp?.description(Component.text(setString(VMotd.motdlist[Random.nextInt(0, VMotd.motdlist.size)])))
        sp?.samplePlayers(*samples.toTypedArray())
        e.ping = sp?.build()
    }

    private fun setString(s : String) : String{
        return s.replace("%playeronline%", getPlayers().toString())
            .replace("%newline%", "\n")
            .replace("%playermax%", VMotd.maxPlayers.toString())
            .replace("&","§")
    }

    private fun getPlayers() : Int{
        if(VMotd.conf.getBoolean("fakePlayers")){
            val playersOnline = when(VMotd.conf.getBoolean("showRealPlayers")){
                true -> VMotd.server.playerCount + VMotd.fakePlayers
                false -> VMotd.fakePlayers
            }
            return playersOnline
        }
        return VMotd.server.playerCount
    }
}