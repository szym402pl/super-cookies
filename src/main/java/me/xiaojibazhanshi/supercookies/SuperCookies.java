package me.xiaojibazhanshi.supercookies;

import me.xiaojibazhanshi.supercookies.commands.SuperCookiesCommand;
import me.xiaojibazhanshi.supercookies.listeners.CookieClickListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SuperCookies extends JavaPlugin {

    ConfigManager configManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);

        getCommand("supercookie").setExecutor(new SuperCookiesCommand(configManager));
        Bukkit.getPluginManager().registerEvents(new CookieClickListener(configManager), this);
    }

    @Override
    public void onDisable() {
        configManager.unloadRecipes();
    }
}
