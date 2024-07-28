package me.xiaojibazhanshi.supercookies;

import me.xiaojibazhanshi.supercookies.objects.CustomCommandObject;
import me.xiaojibazhanshi.supercookies.objects.SuperCookieObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ConfigManager {
    private final SuperCookies main;
    private final ArrayList<SuperCookieObject> cookieList;
    private static boolean removedRecipesDueToAnError;

    public ArrayList<SuperCookieObject> getCookieList() {
        return cookieList;
    }

    public ConfigManager(SuperCookies main) {
        removedRecipesDueToAnError = false;
        cookieList = new ArrayList<>();
        this.main = main;

        loadConfig();
        loadRecipes();
    }

    public void loadConfig() {
        FileConfiguration config = main.getConfig();
        cookieList.clear();

        nullCheckSection("super-cookies");
        for (String cookie : config.getConfigurationSection("super-cookies").getKeys(false)) {
            String prefix = "super-cookies." + cookie + ".";

            // supercookie things
            Material cookieMat = Material.valueOf(config.getString(prefix + "result.item"));
            nullCheck(prefix + "result.item");

            int cookieAmount = config.getInt(prefix + "result.amount");
            nullCheck(prefix + "result.amount");

            String cookieName = config.getString(prefix + "result.name");
            nullCheck(prefix + "result.name");

            ArrayList<String> cookieLore = (ArrayList<String>) config.getStringList(prefix + "result.lore");
            nullCheck(prefix + "result.lore");

            ItemStack cookieItem = new ItemStack(cookieMat, cookieAmount);
            ItemMeta cookieMeta = cookieItem.getItemMeta();

            cookieMeta.setLore(cookieLore);
            cookieMeta.setEnchantmentGlintOverride(true);
            cookieMeta.setDisplayName(cookieName);

            cookieItem.setItemMeta(cookieMeta);

            // recipe things
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(main, cookie), cookieItem);
            prefix += "recipe.";

            nullCheck(prefix + "shape");
            String[] shape = config.getStringList(prefix + "shape").toArray(new String[0]);
            recipe.shape(shape);

            nullCheckSection(prefix + "ingredients");
            for (String presetID : config.getConfigurationSection(prefix + "ingredients").getKeys(false)) {
                String preset = config.getString(prefix + "ingredients" + presetID);
                String[] parts = preset.split(":");
                Material material = Material.getMaterial(parts[0]);
                int amount = Integer.parseInt(parts[1]);

                ItemStack ingredient = new ItemStack(material, amount);
                recipe.setIngredient(presetID.charAt(0), material);
            }

            // on cookie use stuff
            prefix = "super-cookies." + cookie + ".on-use.";

            int levelReq = config.getInt(prefix + "level-requirement");
            nullCheck(prefix + "level-requirement");

            String executor = config.getString(prefix + "command.executor");
            nullCheck(prefix + "command.executor");

            String cmdLine = config.getString(prefix + "command.command-line");
            nullCheck(prefix + "command.command-line");

            CustomCommandObject cmdObj = new CustomCommandObject(levelReq, executor, cmdLine);

            SuperCookieObject superCookie = new SuperCookieObject(cookie, recipe, cookieItem, cmdObj);
            cookieList.add(superCookie);
        }
    }

    public void loadRecipes() {
        cookieList.forEach(cookie -> Bukkit.addRecipe(cookie.recipe()));
    }

    public void unloadRecipes(){
        cookieList.forEach(cookie -> Bukkit.removeRecipe(NamespacedKey.fromString(cookie.name())));
    }

    public void nullCheck(String path) {
        if (main.getConfig().get(path) == null) {
            Bukkit.getLogger().warning("A part of configuration was configured wrong or is missing!");
            Bukkit.getLogger().warning("Missing/Misconfigured part: " + path);

            if (!removedRecipesDueToAnError) {
                Bukkit.getLogger().info("\nRemoving the recipes...");
                removedRecipesDueToAnError = true;
            }

            main.onDisable();
        }
    }

    public void nullCheckSection(String path) {
        if (main.getConfig().getConfigurationSection(path) == null) {
            Bukkit.getLogger().warning("A configuration section was configured wrong or is missing!");
            Bukkit.getLogger().warning("Missing/Misconfigured section: " + path);

            Bukkit.getLogger().info("\nRemoving the recipes...");
            main.onDisable();
        }
    }
}
