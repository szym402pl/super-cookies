package me.xiaojibazhanshi.supercookies.listeners;

import me.xiaojibazhanshi.supercookies.ConfigManager;
import me.xiaojibazhanshi.supercookies.objects.SuperCookieObject;
import me.xiaojibazhanshi.supercookies.SuperCookies;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class CookieClickListener implements Listener {

    private ConfigManager configManager;
    private SuperCookies main;

    public CookieClickListener(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @EventHandler
    public void onCookieClick(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_AIR || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.AIR || !itemInHand.hasItemMeta()
                || !itemInHand.getItemMeta().hasDisplayName()) {
            return;
        }

        String displayName = itemInHand.getItemMeta().getDisplayName();

        Optional<SuperCookieObject> desiredCookieOpt = configManager.getCookieList().stream()
                .filter(cookie -> cookie.cookie().getItemMeta().getDisplayName().equals(displayName))
                .findFirst();

        if (desiredCookieOpt.isEmpty()) {
            return;
        }

        SuperCookieObject desiredCookie = desiredCookieOpt.get();
        int levelReq = desiredCookie.commandObject().levelReq();
        int playerLevel = player.getLevel();

        if (playerLevel < levelReq) {
            int levelDifference = levelReq - playerLevel;
            player.sendMessage(ChatColor.RED + "You need " + ChatColor.GREEN
                    + levelDifference + ChatColor.RED + " more levels to use this cookie!");

            return;
        }

        boolean playerAsCMDExecutor = desiredCookie.commandObject().executor().equalsIgnoreCase("player");

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        player.getInventory().removeItem(desiredCookie.cookie());

        String cmd = desiredCookie.commandObject().command().replace("{player-name}", player.getName());
        boolean wasOpBefore = player.isOp();
        boolean success;

        if (playerAsCMDExecutor) {
            player.setOp(true);
            success = player.performCommand(cmd);
            player.setOp(false);
        } else {
            CommandSender consoleCommandSender = Bukkit.getConsoleSender();
            success = Bukkit.dispatchCommand(consoleCommandSender, cmd);
        }

        if (wasOpBefore)
            player.setOp(true);

        if (!success) {
            player.sendMessage(ChatColor.RED + "An error occured while using the cookie." +
                    "\nPlease report this to the admins.");

            Bukkit.getLogger().info("[SuperCookies] Command execution for player "
                    + player.getName() + " was unsuccessful!");
        }
    }
}
