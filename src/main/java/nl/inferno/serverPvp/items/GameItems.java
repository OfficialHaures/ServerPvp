package nl.inferno.serverPvp.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GameItems {

    public static ItemStack getMurdererKnife() {
        ItemStack knife = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = knife.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Murderer's Knife");
        meta.addEnchant(Enchantment.SHARPNESS, 10, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        knife.setItemMeta(meta);
        return knife;
    }

    public static ItemStack getDetectiveBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Detective's Bow");
        meta.addEnchant(Enchantment.INFINITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        bow.setItemMeta(meta);
        return bow;
    }
}
