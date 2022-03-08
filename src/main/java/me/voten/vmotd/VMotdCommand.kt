package me.voten.vmotd

import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.comphenix.protocol.wrappers.WrappedServerPing
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.io.File
import java.io.FileInputStream
import java.util.*

object VMotdCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if(args?.size!! > 0){
            if(args[0].uppercase() == "RELOAD"){
                val folder = File(VMotd().dataFolder, "/icons")
                if(folder.listFiles() != null) {
                    for (icon in folder.listFiles()) {
                        if (VMotd().isPng(icon)) {
                            VMotd.icons.add(WrappedServerPing.CompressedImage.fromPng(FileInputStream(icon)))
                            println(WrappedServerPing.CompressedImage.fromPng(FileInputStream(icon)))
                        }
                    }
                }
                VMotd.fakeplayers = VMotd().config.getInt("fakePlayersAmount")
                VMotd.textasplayers = VMotd().config.getString("textAsPlayerCount").replace("&","§")
                for(s in VMotd().config.getStringList("motd")){
                    VMotd.motdlist.add(s.replace("&","§"))
                }
                for(s in VMotd().config.getStringList("serverDescription")){
                    VMotd.serverdesc.add(WrappedGameProfile(UUID.randomUUID(), s.replace("&","§")))
                }
                VMotd.maxplayers = VMotd().config.getInt("maxPlayers")
                sender?.sendMessage("§aVMotd Reloaded")
            }
            else{
                sender?.sendMessage("§cUsage: /vmotd reload")
            }
        }else{
            sender?.sendMessage("§cUsage: /vmotd reload")
        }
        return false;
    }

}
