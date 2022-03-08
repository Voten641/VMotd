package me.voten.vmotd

import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.comphenix.protocol.wrappers.WrappedServerPing
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

object VMotdCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if(args?.size!! > 0){
            val folder = VMotd.folder
            Bukkit.getServer().pluginManager.getPlugin("VMotd").reloadConfig()
            val conffile = File(VMotd.folder.parent, "config.yml")
            val conf = YamlConfiguration.loadConfiguration(conffile)
            if(args[0].uppercase() == "RELOAD"){
                if(folder.listFiles() != null) {
                    for (icon in folder.listFiles()) {
                        VMotd.icons.clear()
                        if (isPng(icon)) {
                            VMotd.icons.add(WrappedServerPing.CompressedImage.fromPng(FileInputStream(icon)))
                            println(WrappedServerPing.CompressedImage.fromPng(FileInputStream(icon)))
                        }
                    }
                }
                VMotd.fakeplayers = conf.getInt("fakePlayersAmount")
                VMotd.textasplayers = conf.getString("textAsPlayerCount").replace("&","§")
                VMotd.motdlist.clear()
                VMotd.serverdesc.clear()
                for(s in conf.getStringList("motd")){
                    VMotd.motdlist.add(s.replace("&","§"))
                }
                for(s in conf.getStringList("serverDescription")){
                    VMotd.serverdesc.add(WrappedGameProfile(UUID.randomUUID(), s.replace("&","§")))
                }
                VMotd.maxplayers = conf.getInt("maxPlayers")
                sender?.sendMessage("§aVMotd Reloaded")
                println(VMotd.motdlist)
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
                        conf.save(conffile)
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
        return false
    }

    private fun wrongUsage(sender: CommandSender){
        sender.sendMessage("§cUsage: ")
        sender.sendMessage("§8/vmotd reload")
        sender.sendMessage("§8/vmotd addmotd (text)")
        sender.sendMessage("§8/vmotd delmotd (number)")
        sender.sendMessage("§8/vmotd tempmotd (text)")
    }

    private fun addMotd(sender: CommandSender, save: Boolean, motd: Array<out String>?){
        val conffile = File(VMotd.folder.parent, "config.yml")
        val conf = YamlConfiguration.loadConfiguration(conffile)
        if(motd!!.isNotEmpty()){
            val s : StringBuilder = StringBuilder()
            for(i in motd){
                s.append(" $i")
            }
            VMotd.motdlist.add(s.toString())
            if(save) {
                val m : ArrayList<String> = conf.getStringList("motd") as ArrayList<String>
                m.add(motd.toString())
                conf.set("motd", m)
                conf.save(conffile)
            }
            sender.sendMessage("§aAdded new Motd")
        }else{
            sender.sendMessage("§aUsage: /vmotd addmotd/tempmotd (text)")
        }
    }
    
    @Throws(IOException::class)
    fun isPng(file: File): Boolean {
        FileInputStream(file).use { `is` -> return `is`.read() == 137 }
    }
}
