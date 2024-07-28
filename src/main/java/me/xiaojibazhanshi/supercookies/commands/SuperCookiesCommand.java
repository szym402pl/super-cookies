package me.xiaojibazhanshi.supercookies.commands;

import me.xiaojibazhanshi.supercookies.ConfigManager;
import me.xiaojibazhanshi.supercookies.objects.SuperCookieObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class SuperCookiesCommand implements CommandExecutor {

    private ConfigManager configManager;

    public SuperCookiesCommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    // /supercookie <cookie> <player*>

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only a player can execute this command!");
            return true;
        }

        ChatColor green = ChatColor.GREEN;
        ChatColor red = ChatColor.RED;

        Optional<SuperCookieObject> desiredCookieOpt = configManager.getCookieList().stream()
                .filter(cookie -> cookie.name().equalsIgnoreCase(args[0]))
                .findFirst();

        if (desiredCookieOpt.isEmpty()) {
            player.sendMessage(red + "Specified cookie doesn't exist!");
            return true;
        }

        SuperCookieObject desiredCookie = desiredCookieOpt.get();

        String cookieName = desiredCookie.cookie().getItemMeta().getDisplayName();
        Player target = player;

        switch(args.length) {
            case 1 -> itemAddOrDropWhatever(target, desiredCookie.cookie());

            case 2 -> {
                if (!(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1])))) {
                    player.sendMessage(red + "Specified player isn't online!");
                    return true;
                }

                target = Bukkit.getPlayer(args[1]);
                itemAddOrDropWhatever(target, desiredCookie.cookie());

                player.sendMessage(green + "Successfully given the "
                        + cookieName + green + " to " + target.getName());
            }

            default ->  {
                player.sendMessage(red + command.getUsage());
                return true;
            }
        }

        target.sendMessage(green + "You have received a " + cookieName + green + "!");
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        return true;
    }

    private void itemAddOrDropWhatever(Player target, ItemStack item) {
        if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItem(target.getLocation(), item);
        } else {
            target.getInventory().addItem(item);
        }
    }
}
