package me.xiaojibazhanshi.supercookies.objects;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public record SuperCookieObject(String name, ShapedRecipe recipe, ItemStack cookie, CustomCommandObject commandObject) {}
