package me.voten.vmotd.bukkit

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
import kotlin.collections.ArrayList

object VMotdCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if(args?.size!! > 0){
            Bukkit.getServer().pluginManager.getPlugin("VMotd").reloadConfig()
            val conffile = File(VMotd.folder.parent, "config.yml")
            val conf = YamlConfiguration.loadConfiguration(conffile)
            when(args[0].uppercase()){
                "RELOAD" -> {
                    VMotd.instance.loadConfig()
                    sender?.sendMessage("§aVMotd Reloaded")
                }
                "ADDMOTD" -> addMotd(sender!!, true, args)
                "TEMPMOTD" -> addMotd(sender!!, false, args)
                "DELMOTD" -> {
                    val m : ArrayList<String>  = conf.getStringList("motd") as ArrayList<String>
                    if(args.size > 1){
                        val num : Int = when(val x = args[1].toIntOrNull()){
                            null -> {
                                sender?.sendMessage("§cUsage: /vmotd delmotd (number)")
                                showlist(sender!!, m)
                                return false
                            }
                            else -> x-1
                        }
                        m.removeAt(num)
                        VMotd.motdlist = m
                        conf.set("motd", m)
                        conf.save(conffile)
                        sender?.sendMessage("§eMotd Removed")
                    }else{
                        showlist(sender!!, m)
                    }
                }
                "SETMAXPLAYERS" -> {
                    if(args.size > 1){
                        VMotd.maxplayers = when(val x = args[1].toIntOrNull()){
                            null -> {
                                sender?.sendMessage("§cUsage: /vmotd setmaxplayers (number)")
                                return false
                            }
                            else -> x
                        }
                        conf.set("maxPlayers", VMotd.maxplayers)
                        conf.save(conffile)
                        sender?.sendMessage("§aMax Players Set")
                    }
                    else{
                        sender?.sendMessage("§cUsage: /vmotd setmaxplayers (number)")
                    }
                }
                "SETFAKEPLAYERS" -> {
                    if(args.size > 1){
                        VMotd.fakeplayers = when(val x = args[1].toIntOrNull()){
                            null -> {
                                sender?.sendMessage("§cUsage: /vmotd setfakeplayers (number)")
                                return false
                            }
                            else -> x
                        }
                        conf.set("fakePlayersAmount", VMotd.maxplayers)
                        conf.save(conffile)
                        sender?.sendMessage("§aFake Players Set")
                    }
                    else{
                        sender?.sendMessage("§cUsage: /vmotd setfakeplayers (number)")
                    }
                }
                "ADDSERVERDESC" -> {
                    if(args.size > 2){
                        var num = when(val x = args[1].toIntOrNull()){
                            null -> {
                                sender?.sendMessage("§cUsage: /vmotd addserverdesc (place in the desc) (text)")
                                return false
                            }
                            else -> {
                                if(x-1 > VMotd.serverdesc.size || x < 0){
                                    sender?.sendMessage("§cUsage: /vmotd addserverdesc (place in the desc) (text)")
                                    return false
                                }
                                else{
                                    x
                                }
                            }
                        }
                        num--
                        val s : StringBuilder = StringBuilder()
                        for(i in 2 until args.size){
                            s.append(args[i] + " ")
                        }
                        val desc = ArrayList<WrappedGameProfile>()
                        for(i in 0..VMotd.serverdesc.size){
                            if(i < num){
                                desc.add(VMotd.serverdesc[i])
                            }else if( i == num){
                                desc.add(WrappedGameProfile(UUID.randomUUID(), s.toString()))
                            }else{
                                desc.add(VMotd.serverdesc[i-1])
                            }
                        }
                        VMotd.serverdesc = desc
                        sender?.sendMessage("§aAdded Server Description")
                        sender?.sendMessage("§aNew Description:")
                        for(i in 0 until VMotd.serverdesc.size){
                            sender?.sendMessage("$i  -  " + VMotd.serverdesc[i])
                        }
                    }
                    else{
                        sender?.sendMessage("§cUsage: /vmotd addserverdesc (place in the desc) (text)")
                        sender?.sendMessage("§aServer Description:")
                        for(i in 0 until VMotd.serverdesc.size){
                            sender?.sendMessage("$i  -  " + VMotd.serverdesc[i])
                        }
                    }
                }
                "DELSERVERDESC" -> {
                    val m : ArrayList<String>  = conf.getStringList("serverDescription") as ArrayList<String>
                    if(args.size > 1){
                        val num : Int = when(val x = args[1].toIntOrNull()){
                            null -> {
                                sender?.sendMessage("§cUsage: /vmotd delserverdesc (number)")
                                showlist(sender!!, m)
                                return false
                            }
                            else -> x
                        }
                        m.removeAt(num)
                        for(s in 0 until VMotd.serverdesc.size){
                            if(VMotd.serverdesc[s].name == m[num]){
                                VMotd.serverdesc.removeAt(s)
                            }
                        }
                        conf.set("serverDescription", m)
                        conf.save(conffile)
                        sender?.sendMessage("§eserverDescription Removed")
                    }else{
                        showlist(sender!!, m)
                    }
                }
                "ENABLETEXTASPLAYER" -> {
                    VMotd.textasplayerbol = true
                    conf.set("enableTextAsPlayerCount", true)
                    conf.save(conffile)
                    sender?.sendMessage("§eText as player enabled")
                }
                "DISABLETEXTASPLAYER" -> {
                    VMotd.textasplayerbol = false
                    conf.set("enableTextAsPlayerCount", false)
                    conf.save(conffile)
                    sender?.sendMessage("§eText as player disabled")
                }
                "ENABLEFAKEPLAYERS" -> {
                    VMotd.fakeplayersbol = true
                    conf.set("fakePlayers", true)
                    conf.save(conffile)
                    sender?.sendMessage("§eShow fake players enabled")
                }
                "DISABLEFAKEPLAYERS" -> {
                    VMotd.fakeplayersbol = false
                    conf.set("fakePlayers", false)
                    conf.save(conffile)
                    sender?.sendMessage("§eShow fake players disabled")
                }
                "ENABLESHOWREALPLAYERS" -> {
                    VMotd.showrealplayers = true
                    conf.set("showRealPlayers", true)
                    conf.save(conffile)
                    sender?.sendMessage("§eShow real players enabled")
                }
                "DISABLESHOWREALPLAYERS" -> {
                    VMotd.showrealplayers = false
                    conf.set("showRealPlayers", false)
                    conf.save(conffile)
                    sender?.sendMessage("§eShow real players disabled")
                }
                "ADDTEXTASPLAYERS" -> {
                    if(args.size > 1){
                        val s : StringBuilder = StringBuilder()
                        for(i in 1 until args.size){
                            s.append(args[i] + " ")
                        }
                        VMotd.textasplayers.add(s.toString())
                        sender?.sendMessage("§eText As Players Added")
                        conf.set("textAsPlayerCount", VMotd.textasplayers)
                        conf.save(conffile)
                    }
                    else{
                        sender?.sendMessage("§cUsage: /vmotd addtextasplayers (text)")
                    }
                }
                "DELTEXTASPLAYERS" -> {
                    val m : ArrayList<String>  = conf.getStringList("textAsPlayerCount") as ArrayList<String>
                    if(args.size > 1){
                        val num : Int = when(val x = args[1].toIntOrNull()){
                            null -> {
                                sender?.sendMessage("§cUsage: /vmotd deltextasplayers (number)")
                                showlist(sender!!, m)
                                return false
                            }
                            else -> x
                        }
                        m.removeAt(num)
                        VMotd.textasplayers = m
                        conf.set("textAsPlayerCount", m)
                        conf.save(conffile)
                        sender?.sendMessage("§eText As Players Removed")
                    }
                    else{
                        showlist(sender!!, m)
                    }
                }
                "ADDICON" -> {

                }
                else -> wrongUsage(sender!!)
            }
        }else{
            wrongUsage(sender!!)
        }
        return false
    }

    private fun showlist(sender : CommandSender, list: ArrayList<String>){
        for(i in 0 until list.size){
            sender.sendMessage("$i  -  " + list[i])
        }
    }

    private fun wrongUsage(sender: CommandSender){
        sender.sendMessage("§8/vmotd reload")
        sender.sendMessage("§8/vmotd addmotd (text)")
        sender.sendMessage("§8/vmotd delmotd (number)")
        sender.sendMessage("§8/vmotd tempmotd (text)")
        sender.sendMessage("§8/vmotd setmaxplayers (number)")
        sender.sendMessage("§8/vmotd setfakeplayers (number)")
        sender.sendMessage("§8/vmotd addserverdesc (place in the desc) (text)")
        sender.sendMessage("§8/vmotd delserverdesc (number)")
        sender.sendMessage("§8/vmotd enabletextasplaye")
        sender.sendMessage("§8/vmotd disabletextasplayer")
        sender.sendMessage("§8/vmotd enablefakeplayers")
        sender.sendMessage("§8/vmotd disablefakeplayers")
        sender.sendMessage("§8/vmotd enableshowrealplayers")
        sender.sendMessage("§8/vmotd disableshowrealplayers")
        sender.sendMessage("§8/vmotd addtextasplayers (text)")
        sender.sendMessage("§8/vmotd deltextasplayers (number)")
    }

    private fun addMotd(sender: CommandSender, save: Boolean, motd2: Array<out String>?){
        val motd : ArrayList<String> = ArrayList()
        for(i in 1 until motd2?.size!!){
            motd.add(motd2[i])
        }
        val conffile = File(VMotd.folder.parent, "config.yml")
        val conf = YamlConfiguration.loadConfiguration(conffile)
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
                conf.save(conffile)
            }
            sender.sendMessage("§aAdded new Motd")
        }else{
            sender.sendMessage("§aUsage: /vmotd addmotd/tempmotd (text)")
        }
    }
}
