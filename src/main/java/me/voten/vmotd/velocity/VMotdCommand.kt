package me.voten.vmotd.velocity

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.util.Favicon
import net.kyori.adventure.text.Component
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO

object VMotdCommand : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation?) {
        val args = invocation?.arguments()
        val sender = invocation?.source()
        if(args?.size!! > 0){
            val folder = VMotd.folder
            val conffile = File(VMotd.folder.parent, "config.yml")
            val conf = VMotd.conf
            if(args[0].uppercase() == "RELOAD"){
                if(folder.listFiles() != null) {
                    for (icon in folder.listFiles()) {
                        VMotd.icons.clear()
                        if (isPng(icon)) {
                            VMotd.icons.add(Favicon.create(ImageIO.read(icon)))
                        }
                    }
                }
                VMotd.fakePlayers = conf.getLong("fakePlayersAmount").toInt()
                VMotd.textAsPlayers = conf.getString("textAsPlayerCount").replace("&","§")
                VMotd.motdlist.clear()
                VMotd.serverDesc.clear()
                for(s in conf.getList<String>("motd")){
                    VMotd.motdlist.add(s.replace("&","§"))
                }
                for(s in conf.getList<String>("serverDescription")){
                    VMotd.serverDesc.add(s.replace("&","§"))
                }
                VMotd.maxPlayers = conf.getLong("maxPlayers").toInt()
                sender?.sendMessage(Component.text("§aVMotd Reloaded"))
            }
            else if(args[0].uppercase() == "ADDMOTD"){
                addMotd(sender!!, true, args)
            }
            else if(args[0].uppercase() == "DELMOTD"){
                val m : ArrayList<String> = conf.getList<String>("motd") as ArrayList<String>
                if(args.size > 1){
                    val num : Int = when(args[1].toIntOrNull()){
                        null -> -20
                        else -> args[1].toInt()
                    }
                    if(num == -20){
                        sender?.sendMessage(Component.text("§aSelect which motd u want to delete"))
                        for(i in 0 until m.size){
                            sender?.sendMessage(Component.text("$i   -   " + m[i]))
                        }
                    }else{
                        m.removeAt(num)
                        //conf.set("motd", m)
                        VMotd.motdlist.removeAt(num)
                        //ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
                        sender?.sendMessage(Component.text("§aMotd removed"))
                    }
                }else {
                    sender?.sendMessage(Component.text("§aSelect which motd u want to delete"))
                    for(i in 0 until m.size){
                        sender?.sendMessage(Component.text("$i   -   " + m[i]))
                    }
                }
            }
            else if(args[0].uppercase() == "TEMPMOTD"){
                addMotd(sender!!, false, args)
            }
            else{
                wrongUsage(sender!!)
            }
        }else{
            wrongUsage(sender!!)
        }
    }

    private fun wrongUsage(sender: CommandSource){
        sender.sendMessage(Component.text("§cUsage: "))
        sender.sendMessage(Component.text("§8/vmotd reload"))
        sender.sendMessage(Component.text("§8/vmotd addmotd (text)"))
        sender.sendMessage(Component.text("§8/vmotd delmotd (number)"))
        sender.sendMessage(Component.text("§8/vmotd tempmotd (text)"))
    }

    private fun addMotd(sender: CommandSource, save: Boolean, motd2: Array<out String>?){
        val motd : ArrayList<String> = ArrayList()
        for(i in 1 until motd2?.size!!){
            motd.add(motd2[i])
        }
        val conffile = File(VMotd.folder.parent, "config.yml")
        val conf = VMotd.conf
        if(motd.isNotEmpty()){
            val s : StringBuilder = StringBuilder()
            for(i in motd){
                s.append("$i ")
            }
            VMotd.motdlist.add(s.toString())
            if(save) {
                val m : ArrayList<String> = conf.getList<String>("motd") as ArrayList<String>
                m.add(motd.toString())
                //conf.set("motd", m)
                //ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
            }
            sender.sendMessage(Component.text("§aAdded new Motd"))
        }else{
            sender.sendMessage(Component.text("§aUsage: /vmotd addmotd/tempmotd (text)"))
        }
    }

    @Throws(IOException::class)
    fun isPng(file: File): Boolean {
        FileInputStream(file).use { `is` -> return `is`.read() == 137 }
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
}