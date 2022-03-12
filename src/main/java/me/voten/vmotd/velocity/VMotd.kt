package me.voten.vmotd.velocity

import com.google.inject.Inject
import com.moandjiezana.toml.Toml
import com.velocitypowered.api.command.Command
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.util.Favicon
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Level
import java.util.logging.Logger
import javax.imageio.ImageIO


@Plugin(id= "bmotd", name = "VMotd", version = "VERSION")
class VMotd {
    companion object{
        lateinit var instance : VMotd
        var motdlist : ArrayList<String> = ArrayList()
        var maxPlayers : Int = 0
        var fakePlayers : Int = 0
        var serverDesc : ArrayList<String> = ArrayList()
        var icons : ArrayList<Favicon> = ArrayList()
        lateinit var textAsPlayers : String
        lateinit var folder : File
        lateinit var server : ProxyServer
        lateinit var dataDirectory : Path
        lateinit var conf : Toml
        lateinit var logger : Logger
    }

    @Inject
    fun VelocityVMotd(server2: ProxyServer, @DataDirectory dataDirectory2: Path, logger2: Logger){
        server = server2
        dataDirectory = dataDirectory2
        logger = logger2
    }

    @Subscribe
    fun onInitialize(e: ProxyInitializeEvent){
        server.eventManager.register(this, PingEvent)
        conf = loadConfig(dataDirectory)!!
        instance = this
        folder = File(dataDirectory.toFile(), "/icons")
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
        fakePlayers = conf.getLong("fakePlayersAmount").toInt()
        textAsPlayers = conf.getString("textAsPlayerCount").replace("&","§")
        for(s in conf.getList<String>("motd")){
            motdlist.add(s.replace("&","§"))
        }
        for(s in conf.getList<String>("serverDescription")){
            serverDesc.add(s.replace("&","§"))
        }
        maxPlayers = conf.getLong("maxPlayers").toInt()
        UpdateChecker().checkVersion()
        server.commandManager.register(server.commandManager.metaBuilder("vmotd").aliases("vm" ).build(), VMotdCommand as Command)
    }

    private fun loadConfig(path: Path): Toml? {
        val folder = path.toFile()
        val file = File(folder, "config.toml")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        if (!file.exists()) {
            try {
                javaClass.getResourceAsStream("/" + file.name).use { input ->
                    if (input != null) {
                        Files.copy(input, file.toPath())
                    } else {
                        file.createNewFile()
                    }
                }
            } catch (exception: IOException) {
                exception.printStackTrace()
                return null
            }
        }
        return Toml().read(file)
    }

    @Throws(IOException::class)
    public fun isPng(file: File): Boolean {
        FileInputStream(file).use { `is` -> return `is`.read() == 137 }
    }
}