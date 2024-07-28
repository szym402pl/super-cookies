package me.xiaojibazhanshi.supercookies.commands;

import me.xiaojibazhanshi.supercookies.ConfigManager;
import me.xiaojibazhanshi.supercookies.SuperCookies;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SuperCookiesReloadCommand implements CommandExecutor {

    private ConfigManager configManager;
    private SuperCookies main;

    public SuperCookiesReloadCommand(ConfigManager configManager, SuperCookies main) {
        this.configManager = configManager;
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length > 0) {

            String message = ChatColor.RED + "Usage: " +
                    (sender instanceof Player player ? "/" : "") + "supercookiesreload";

            sender.sendMessage(message);
            return false;
        }

        try {
            main.reloadConfig();
            configManager.loadConfig();
            configManager.loadRecipes();
            sender.sendMessage(ChatColor.GREEN + "[SuperCookies] Successfully reloaded the config!");
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "[SuperCookies] Something went wrong while reloading the config!" +
                    "\nCheck the console for the details!");
            ex.printStackTrace();
        }

        return false;
    }
}
