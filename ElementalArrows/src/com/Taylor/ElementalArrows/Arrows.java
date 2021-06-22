package com.Taylor.ElementalArrows;

import org.bukkit.Material;

import net.md_5.bungee.api.ChatColor;

public enum Arrows {
	FIRE("#f77800", "Fire Arrow", "fire_arrow", Material.FLINT),
	EXPLOSION("#b80600", "Explosive Arrow", "explosive_arrow", Material.GUNPOWDER),
	LIGHTNING("#cf13e8", "Lightning Arrow", "lightning_arrow", Material.IRON_NUGGET),
	ICE("#d6fffc", "Ice Arrow", "ice_arrow", Material.SNOWBALL);
	
	private String name, key, color;
	private Material item;
	private Arrows(String color, String name, String key, Material item) {
		this.name = name;
		this.item = item;
		this.key = key;
		this.color = color;
	}
	
	public String getColorName() {
		return color;
	}
	
	public ChatColor getColor() {
		return ChatColor.of(color);
	}
	
	public String getName() {
		return name;
	}
	
	public String getKey() {
		return key;
	}
	
	public Material getMaterial() {
		return item;
	}
}