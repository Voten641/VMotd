package me.voten.vmotd.bungee

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import kotlin.collections.ArrayList

object VMotdCommand : Command("vmotd") {
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if(args?.size!! > 0){
            val folder = VMotd.folder
            val conffile = File(VMotd.folder.parent, "config.yml")
            val conf = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(conffile)
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
                                return
                            }
                            else -> x-1
                        }
                        m.removeAt(num)
                        VMotd.motdlist = m
                        conf.set("motd", m)
                        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
                        sender?.sendMessage("§eMotd Removed")
                    }else{
                        showlist(sender!!, m)
                    }
                }
                "SETMAXPLAYERS" -> {
                    if(args.size > 1){
                        VMotd.maxPlayers = when(val x = args[1].toIntOrNull()){
                            null -> {
                                sender?.sendMessage("§cUsage: /vmotd setmaxplayers (number)")
                                return
                            }
                            else -> x
                        }
                        conf.set("maxPlayers", VMotd.maxPlayers)
                        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
                        sender?.sendMessage("§aMax Players Set")
                    }
                    else{
                        sender?.sendMessage("§cUsage: /vmotd setmaxplayers (number)")
                    }
                }
                "SETFAKEPLAYERS" -> {
                    if(args.size > 1){
                        VMotd.fakePlayers = when(val x = args[1].toIntOrNull()){
                            null -> {
                                sender?.sendMessage("§cUsage: /vmotd setfakeplayers (number)")
                                return
                            }
                            else -> x
                        }
                        conf.set("fakePlayersAmount", VMotd.maxPlayers)
                        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
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
                                return
                            }
                            else -> {
                                if(x-1 > VMotd.serverDesc.size || x < 0){
                                    sender?.sendMessage("§cUsage: /vmotd addserverdesc (place in the desc) (text)")
                                    return
                                }
                                else{
                                    x
                                }
                            }
                        }
                        val s : StringBuilder = StringBuilder()
                        for(i in 2 until args.size){
                            s.append(args[i] + " ")
                        }
                        val desc = ArrayList<String>()
                        for(i in 0..VMotd.serverDesc.size){
                            if(i < num){
                                desc.add(VMotd.serverDesc[i])
                            }else if( i == num){
                                desc.add(s.toString())
                            }else{
                                desc.add(VMotd.serverDesc[i-1])
                            }
                        }
                        VMotd.serverDesc = desc
                        conf.set("serverDescription", desc)
                        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
                        sender?.sendMessage("§aAdded Server Description")
                        sender?.sendMessage("§aNew Description:")
                        for(i in 0 until VMotd.serverDesc.size){
                            sender?.sendMessage("$i  -  " + VMotd.serverDesc[i])
                        }
                    }
                    else{
                        sender?.sendMessage("§cUsage: /vmotd addserverdesc (place in the desc) (text)")
                        sender?.sendMessage("§aServer Description:")
                        for(i in 0 until VMotd.serverDesc.size){
                            sender?.sendMessage("$i  -  " + VMotd.serverDesc[i])
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
                                return
                            }
                            else -> x
                        }
                        m.removeAt(num)
                        VMotd.serverDesc = m
                        conf.set("serverDescription", m)
                        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
                        sender?.sendMessage("§eServer Description Removed")
                    }else{
                        showlist(sender!!, m)
                    }
                }
                "ENABLETEXTASPLAYER" -> {
                    VMotd.textAsPlayerbol = true
                    conf.set("enableTextAsPlayerCount", true)
                    ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
                    sender?.sendMessage("§eText as player enabled")
                }
                "DISABLETEXTASPLAYER" -> {
                    VMotd.textAsPlayerbol = false
                    conf.set("enableTextAsPlayerCount", false)
                    ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
                    sender?.sendMessage("§eText as player disabled")
                }
                "ENABLEFAKEPLAYERS" -> {
                    VMotd.fakePlayersbol = true
                    conf.set("fakePlayers", true)
                    ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
                    sender?.sendMessage("§eShow fake players enabled")
                }
                "DISABLEFAKEPLAYERS" -> {
                    VMotd.fakePlayersbol = false
                    conf.set("fakePlayers", false)
                    ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
                    sender?.sendMessage("§eShow fake players disabled")
                }
                "ENABLESHOWREALPLAYERS" -> {
                    VMotd.showRealPlayers = true
                    conf.set("showRealPlayers", true)
                    ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
                    sender?.sendMessage("§eShow real players enabled")
                }
                "DISABLESHOWREALPLAYERS" -> {
                    VMotd.showRealPlayers = false
                    conf.set("showRealPlayers", false)
                    ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
                    sender?.sendMessage("§eShow real players disabled")
                }
                "ADDTEXTASPLAYERS" -> {
                    if(args.size > 1){
                        val s : StringBuilder = StringBuilder()
                        for(i in 1 until args.size){
                            s.append(args[i] + " ")
                        }
                        VMotd.textAsPlayers.add(s.toString())
                        sender?.sendMessage("§eText As Players Added")
                        conf.set("textAsPlayerCount", VMotd.textAsPlayers)
                        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
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
                                return
                            }
                            else -> x
                        }
                        m.removeAt(num)
                        VMotd.textAsPlayers = m
                        conf.set("textAsPlayerCount", VMotd.textAsPlayers)
                        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(conf, conffile)
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

    private fun showlist(sender : CommandSender, list: ArrayList<String>){
        for(i in 0 until list.size){
            sender.sendMessage("$i  -  " + list[i])
        }
    }
}