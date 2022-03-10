package me.voten.vmotd.bungee

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.Favicon
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.regex.Pattern
import javax.imageio.ImageIO
import kotlin.collections.ArrayList

object VMotdCommand : Command("vmotd") {
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if(args?.size!! > 0){
            val folder = VMotd.folder
            val conffile = File(VMotd.folder.parent, "config.yml")
            val conf = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(conffile)
            if(args[0].uppercase() == "RELOAD"){
                if(folder.listFiles() != null) {
                    for (icon in folder.listFiles()) {
                        VMotd.icons.clear()
                        if (isPng(icon)) {
                            VMotd.icons.add(Favicon.create(ImageIO.read(icon)))
                        }
                    }
                }
                VMotd.fakePlayers = conf.getInt("fakePlayersAmount")
                VMotd.textAsPlayers = conf.getString("textAsPlayerCount").replace("&","§")
                VMotd.motdlist.clear()
                VMotd.serverDesc.clear()
                for(s in conf.getStringList("motd")){
                    VMotd.motdlist.add(s.replace("&","§"))
                }
                for(s in conf.getStringList("serverDescription")){
                    VMotd.serverDesc.add(s.replace("&","§"))
                }
                VMotd.maxPlayers = conf.getInt("maxPlayers")
                sender?.sendMessage("§aVMotd Reloaded")
            }
            else if(args[0].uppercase() == "ADDMOTD"){
                addMotd(sender!!, true, args)
            }
            else if(args[0].uppercase() == "DELMOTD"){
                val m : ArrayList<String> = conf.getStringList("motd") as ArrayList<String>
                if(args.size > 1){
                    val num : Int = when(args[1].toIntOrNull()){
                        null -> -20
                        else -> args[1].toInt()
                    }
                    if(num == -20){
                        sender?.sendMessage("§aSelect which motd u want to delete")
                        for(i in 0 until m.size){
                            sender?.sendMessage("$i   -   " + m[i])
                        }
                    }else{
                        m.removeAt(num)
                        conf.set("motd", m)
                        VMotd.motdlist.removeAt(num)
                        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
                        sender?.sendMessage("§aMotd removed")
                    }
                }else {
                    sender?.sendMessage("§aSelect which motd u want to delete")
                    for(i in 0 until m.size){
                        sender?.sendMessage("$i   -   " + m[i])
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

    private fun wrongUsage(sender: CommandSender){
        sender.sendMessage("§cUsage: ")
        sender.sendMessage("§8/vmotd reload")
        sender.sendMessage("§8/vmotd addmotd (text)")
        sender.sendMessage("§8/vmotd delmotd (number)")
        sender.sendMessage("§8/vmotd tempmotd (text)")
    }

    private fun addMotd(sender: CommandSender, save: Boolean, motd2: Array<out String>?){
        val motd : ArrayList<String> = ArrayList()
        for(i in 1 until motd2?.size!!){
            motd.add(motd2[i])
        }
        val conffile = File(VMotd.folder.parent, "config.yml")
        val conf = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(conffile)
        if(motd.isNotEmpty()){
            val s : StringBuilder = StringBuilder()
            for(i in motd){
                s.append("$i ")
            }
            VMotd.motdlist.add(s.toString())
            if(save) {
                val m : ArrayList<String> = conf.getStringList("motd") as ArrayList<String>
                m.add(motd.toString())
                conf.set("motd", m)
                ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
            }
            sender.sendMessage("§aAdded new Motd")
        }else{
            sender.sendMessage("§aUsage: /vmotd addmotd/tempmotd (text)")
        }
    }

    private fun setString(s : String) : String{
        return s.replace("%playeronline%", getPlayers().toString())
            .replace("%newline%", "\n")
            .replace("%playermax%", VMotd.maxPlayers.toString())
            .replace("&","§")
    }

    @Throws(IOException::class)
    fun isPng(file: File): Boolean {
        FileInputStream(file).use { `is` -> return `is`.read() == 137 }
    }

    private fun getPlayers() : Int{
        if(VMotd.conf.getBoolean("fakePlayers")){
            val playersOnline = when(VMotd.conf.getBoolean("showRealPlayers")){
                true -> ProxyServer.getInstance().onlineCount + VMotd.fakePlayers
                false -> VMotd.fakePlayers
            }
            return playersOnline
        }
        return ProxyServer.getInstance().onlineCount
    }
}