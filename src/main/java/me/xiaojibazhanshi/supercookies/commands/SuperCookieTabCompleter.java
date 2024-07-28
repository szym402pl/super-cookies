package me.xiaojibazhanshi.supercookies.commands;

import me.xiaojibazhanshi.supercookies.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SuperCookieTabCompleter implements TabCompleter {

    ConfigManager configManager;

    public SuperCookieTabCompleter(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        ArrayList<String> results = new ArrayList<>();

        if (args.length == 1) {

            configManager.getCookieList().forEach(cookie -> results.add(cookie.name()));
            return StringUtil.copyPartialMatches(args[0], results, new ArrayList<>());

        } else if (args.length == 2) {

            Bukkit.getOnlinePlayers().forEach(player -> results.add(player.getName()));
            return StringUtil.copyPartialMatches(args[1],results, new ArrayList<>());

        }

        return new ArrayList<>();
    }
}
