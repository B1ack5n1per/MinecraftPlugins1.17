package com.Taylor.ElementalArrows;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin {
	public Recipes recipes;

	public static HashMap<Arrow, Player> arrows = new HashMap<Arrow, Player>();
	public static HashMap<Arrow, Double> times = new HashMap<Arrow, Double>();
	public static final double DELAY = 10;
	
	@Override
	public void onEnable() {
		recipes = new Recipes(this);
		this.getServer().getPluginManager().registerEvents(new Listeners(this),this);

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
	}

	@Override
	public void onDisable() {

	}
}
