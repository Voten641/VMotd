package me.voten.vmotd.velocity

import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import com.velocitypowered.api.event.proxy.ProxyPingEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import java.nio.file.Path

@Plugin(id= "VMotd", name = "VMotd", version = "1.3")
class VMotd {
    lateinit var server : ProxyServer
    lateinit var dataDirectory : Path

    @Inject
    fun VelocityVMotd(server: ProxyServer, @DataDirectory dataDirectory: Path){
        this.server = server
        this.dataDirectory = dataDirectory
    }

    @Subscribe
    fun onPingEvent(e: ProxyPingEvent){

    }
}