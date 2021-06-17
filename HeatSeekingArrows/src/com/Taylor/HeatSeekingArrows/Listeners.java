package com.Taylor.HeatSeekingArrows;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.meta.ItemMeta;


public class Listeners implements Listener {
	
	public HashSet<Arrow> arrows;
	
	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		ItemMeta arrow = e.getConsumable().getItemMeta();
		if (arrow != null && arrow.getDisplayName().equals(ChatColor.RED + "Heat Seeking Arrow") && e.getEntity() instanceof Player) {
			Main.arrows.put((Arrow) e.getProjectile(), (Player) e.getEntity());
			Main.times.put((Arrow) e.getProjectile(), 0d);
		}
	}
	
	@EventHandler
	public void onHit(ProjectileHitEvent e) {
		if (e.getEntity() instanceof Arrow) {
			if (Main.arrows.keySet().contains((Arrow) e.getEntity())) {
				Main.arrows.remove((Arrow) e.getEntity());
				Main.times.remove((Arrow) e.getEntity());
			}
		}
	}
}
