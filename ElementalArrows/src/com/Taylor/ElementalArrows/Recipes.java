package com.Taylor.ElementalArrows;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;


public class Recipes {
	private JavaPlugin plugin;
	public ArrayList<ShapelessRecipe> arrowRecipes = new ArrayList<ShapelessRecipe>();
	
	public Recipes(JavaPlugin plugin) {
		this.plugin = plugin;
		for (Arrows arrow: Arrows.values()) {
			ShapelessRecipe recipe = getArrowRecipe(arrow);
			Bukkit.addRecipe(recipe);
			arrowRecipes.add(recipe);
		}
		
		ShapelessRecipe recipe = getMissileArrowRecipe();
		Bukkit.addRecipe(recipe);
		arrowRecipes.add(recipe);
		
	}
	
	public ShapelessRecipe getArrowRecipe(Arrows arrow) {
		ItemStack item = new ItemStack(Material.TIPPED_ARROW);
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(arrow.getColor() + arrow.getName());
		java.awt.Color color = arrow.getColor().getColor();
		meta.setColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
		item.setItemMeta(meta);
		
		NamespacedKey key = new NamespacedKey(plugin, arrow.getKey());
		
		ShapelessRecipe recipe = new ShapelessRecipe(key, item);
		recipe.addIngredient(Material.ARROW);
		recipe.addIngredient(Material.SLIME_BALL);
		recipe.addIngredient(arrow.getMaterial());
		return recipe;
	}
	
	public ShapelessRecipe getMissileArrowRecipe() {
		ItemStack item = new ItemStack(Material.TIPPED_ARROW);
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(ChatColor.BLACK + "Missile Arrow");
		java.awt.Color color = ChatColor.of("#000000").getColor();
		meta.setColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
		item.setItemMeta(meta);
		
		NamespacedKey key = new NamespacedKey(plugin, "missile");
		
		ShapelessRecipe recipe = new ShapelessRecipe(key, item);
		recipe.addIngredient(Material.ARROW);
		recipe.addIngredient(Material.SLIME_BALL);
		recipe.addIngredient(Material.GUNPOWDER);
		recipe.addIngredient(Material.REDSTONE);
		recipe.addIngredient(Material.GOLD_NUGGET);
		return recipe;
	}
}
