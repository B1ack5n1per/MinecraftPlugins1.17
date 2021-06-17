package com.Taylor.Lockout;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Color {
	RED, ORANGE, YELLOW, GREEN, LIGHT_BLUE, BLUE, PURPLE;
	
	public static Color fromItem(ItemStack item) {
		switch(item.getType()) {
			case RED_WOOL:
				return RED;
			case LIME_WOOL:
				return GREEN;
			case BLUE_WOOL:
				return BLUE;
			case YELLOW_WOOL:
				return YELLOW;
			case ORANGE_WOOL:
				return ORANGE;
			case LIGHT_BLUE_WOOL:
				return LIGHT_BLUE;
			case PURPLE_WOOL:
				return PURPLE;
			default:
				return null;
		}
	}
	
	public static Material toPane(Color col) {
		switch(col) {
			case RED:
				return Material.RED_STAINED_GLASS_PANE;
			case GREEN:
				return Material.LIME_STAINED_GLASS_PANE;
			case BLUE:
				return Material.BLUE_STAINED_GLASS_PANE;
			case YELLOW:
				return Material.YELLOW_STAINED_GLASS_PANE;
			case ORANGE:
				return Material.ORANGE_STAINED_GLASS_PANE;
			case LIGHT_BLUE:
				return Material.LIGHT_BLUE_STAINED_GLASS_PANE;
			case PURPLE:
				return Material.PURPLE_STAINED_GLASS_PANE;
		}
		return null;
	}
	
	public static ItemStack toItem(Color col) {
		ItemStack res = null;
		switch(col) {
			case RED:
				res = new ItemStack(Material.RED_WOOL);
				break;
			case GREEN:
				res = new ItemStack(Material.LIME_WOOL);
				break;
			case BLUE:
				res = new ItemStack(Material.BLUE_WOOL);
				break;
			case YELLOW:
				res = new ItemStack(Material.YELLOW_WOOL);
				break;
			case ORANGE:
				res = new ItemStack(Material.ORANGE_WOOL);
				break;
			case LIGHT_BLUE:
				res = new ItemStack(Material.LIGHT_BLUE_WOOL);
				break;
			case PURPLE:
				res = new ItemStack(Material.PURPLE_WOOL);
				break;
		}
		ItemMeta meta = res.getItemMeta();
		meta.setDisplayName("Claim " + col.toString().toLowerCase());
		res.setItemMeta(meta);
		return res;
	}
}
