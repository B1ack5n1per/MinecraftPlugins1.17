package com.Taylor.HeatSeekingArrows;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin {
	
	public static HashMap<Arrow, Player> arrows = new HashMap<Arrow, Player>();
	public static HashMap<Arrow, Double> times = new HashMap<Arrow, Double>();
	public static final double DELAY = 10;
	
	@Override
	public void onEnable() {
		ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, "seeking_arrow"), genArrow());
		recipe.addIngredient(Material.ARROW);
		recipe.addIngredient(Material.REDSTONE);
		recipe.addIngredient(Material.GOLD_NUGGET);
		Bukkit.addRecipe(recipe);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (Arrow arrow : arrows.keySet()) {
					times.put(arrow, times.get(arrow) + 1);
					if (times.get(arrow) < DELAY) continue;
					List<Entity> entities = arrow.getNearbyEntities(20, 20, 20);
					entities.remove(arrows.get(arrow));
					LivingEntity nearest = null;
					for (Entity entity : entities) {
						if (entity instanceof LivingEntity) { 
							if (nearest == null || entity.getLocation().distance(arrow.getLocation()) < nearest.getLocation().distance(arrow.getLocation())) {
								nearest = (LivingEntity) entity;
							}
						}
					}
					if (nearest != null) arrow.setVelocity(nearest.getLocation().subtract(arrow.getLocation()).toVector().normalize().multiply(new Vector(1, 0.25, 1)));
					
				}
			}
			
		}, 1, 1);
		this.getServer().getPluginManager().registerEvents(new Listeners(), this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	private ItemStack genArrow() {
		ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
		PotionMeta data = (PotionMeta) arrow.getItemMeta();
		data.setDisplayName(ChatColor.RED + "Heat Seeking Arrow");
		data.setColor(Color.fromRGB(230, 46, 0));
		arrow.setItemMeta(data);
		
		return arrow;
	}
}
