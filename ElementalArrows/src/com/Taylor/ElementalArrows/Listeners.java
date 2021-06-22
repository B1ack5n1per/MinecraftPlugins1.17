package com.Taylor.ElementalArrows;

import org.bukkit.ChatColor;

//import java.util.ArrayList;

//import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
//import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
//import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
//import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
//import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Listeners implements Listener {
	private Main plugin;
	
	public Listeners(Main plugin) {
		this.plugin = plugin;
	}
	
	/* Arrow Changer
	@EventHandler
	public void onChangeItem(PlayerItemHeldEvent e) {
		ItemStack bow = null;
		for (int i = 8; i >= 0; i--) if (e.getPlayer().getInventory().getItem(i) != null && e.getPlayer().getInventory().getItem(i).getType() == Material.CROSSBOW) bow = e.getPlayer().getInventory().getItem(i);
		if (e.getPlayer().getInventory().getItemInOffHand() != null && e.getPlayer().getInventory().getItemInOffHand().getType() == Material.CROSSBOW) bow = e.getPlayer().getInventory().getItemInOffHand();
		if (bow != null) {
			ItemStack newItem = e.getPlayer().getInventory().getItem(e.getNewSlot());
			if (newItem != null) {
				if (newItem.getType() == Material.ARROW || newItem.getType() == Material.TIPPED_ARROW || newItem.getType() == Material.SPECTRAL_ARROW) {
					CrossbowMeta meta = (CrossbowMeta) bow.getItemMeta();
					if (meta.getChargedProjectiles().size() > 0) {
						boolean found = false;
						ItemStack search = meta.getChargedProjectiles().get(0);
						if (!newItem.equals(search)) {
							for (ItemStack inv: e.getPlayer().getInventory()) {
								if (inv != null && inv.getItemMeta().equals(search.getItemMeta()) && search.getType() == inv.getType() && inv.getAmount() < 64) {
									inv.setAmount(inv.getAmount() + 1);
									ArrayList<ItemStack> loaded = new ArrayList<ItemStack>();
									loaded.clear();
									ItemStack clone = newItem.clone();
									clone.setAmount(1);
									loaded.add(clone);
									meta.setChargedProjectiles(loaded);
									bow.setItemMeta(meta);
									newItem.setAmount(newItem.getAmount() - 1);
									found = true;
									break;
								}
							}
							if (!found) e.getPlayer().getInventory().addItem(meta.getChargedProjectiles().get(0));
							String message = "";
							if (newItem.getItemMeta() != null && newItem.getItemMeta().getDisplayName().length() > 0) message += newItem.getItemMeta().getDisplayName();
							else if (newItem.getType() == Material.TIPPED_ARROW) {
								PotionMeta potMeta = (PotionMeta) newItem.getItemMeta();
								String[] temp = potMeta.getBasePotionData().getType().toString().split("_");
								message += ChatColor.GRAY + "Arrow of ";
								for (int i = 0; i < temp.length; i++) {
									message += temp[i].substring(0, 1) + temp[i].substring(1, temp[i].length()).toLowerCase();
									if (i < temp.length - 2) message += " ";
								}
							}
							else {
								String[] temp = newItem.getType().toString().split("_");
								message += ChatColor.GRAY;
								for (int i = 0; i < temp.length; i++) {
									message += temp[i].substring(0, 1) + temp[i].substring(1, temp[i].length()).toLowerCase();
									if (i < temp.length - 2) message += " ";
								}
							}
							e.getPlayer().sendMessage(ChatColor.ITALIC + "" + ChatColor.DARK_GRAY + "Loaded: " + ChatColor.RESET +  message);
						}
					}
				}
			}
		}
	}
	*/
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		for (ShapelessRecipe recipe: plugin.recipes.arrowRecipes) e.getPlayer().discoverRecipe(recipe.getKey());
	}
	
	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		ItemMeta meta = e.getConsumable().getItemMeta();
		if (e.getEntity() instanceof Player) {
			for (Arrows arrow: Arrows.values()) {
				if (meta.getDisplayName().contains(arrow.getName()) && meta.getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
					e.getProjectile().setCustomName(arrow.getName());
				}
			}

			if (meta != null && meta.getDisplayName().equals(ChatColor.BLACK + "Missile Arrow")) {
				Main.arrows.put((Arrow) e.getProjectile(), (Player) e.getEntity());
				Main.times.put((Arrow) e.getProjectile(), 0d);
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent e) {
		if (e.getEntity().getType() == EntityType.ARROW) {
			Arrow arrow = (Arrow) e.getEntity();
			if (arrow.getCustomName() != null) {
				if (arrow.getCustomName().equals("fireSpawner")) startFire(e);
				if (arrow.getCustomName().equals(Arrows.FIRE.getName())) fireEffect(e);
				if (arrow.getCustomName().equals(Arrows.ICE.getName())) iceEffect(e);
				if (arrow.getCustomName().equals(Arrows.EXPLOSION.getName())) explosionEffect(e);
				if (arrow.getCustomName().equals(Arrows.LIGHTNING.getName())) lightningEffect(e);
			}
			if (Main.arrows.keySet().contains((Arrow) e.getEntity())) {
				Main.arrows.remove((Arrow) e.getEntity());
				Main.times.remove((Arrow) e.getEntity());
				explosionEffect(e);
			}
		}
	}
	
	private void startFire(ProjectileHitEvent e) {
		Location loc = null;
		if (e.getHitEntity() != null) loc = e.getHitEntity().getLocation();
		if (e.getHitBlock() != null) loc = e.getHitBlock().getLocation();
		if (loc != null) e.getEntity().getWorld().getBlockAt(loc.clone().add(0, 1, 0)).setType(Material.FIRE);
		e.getEntity().remove();
	}
	
	private void fireEffect(ProjectileHitEvent e) {
		Location loc = e.getEntity().getLocation();
		World world = e.getEntity().getWorld();
		for (int i = 1; i < 4; i++) {
			for (int j = 0; j < 12; j++) {
				double angle = j * Math.PI / 6;
				Vector velocity = new Vector(3 * Math.sin(angle), -3, 3 * Math.cos(angle));
				Arrow arrow = world.spawnArrow(loc.clone().add(0.5, i , 0.5), velocity, 50, 0);
				arrow.setCustomName("fireSpawner");
				arrow.setSilent(true);
				arrow.setDamage(0);
			}
		}
		e.getEntity().remove();
	}
	
	private void iceEffect(ProjectileHitEvent e) {
		World world = e.getEntity().getWorld();
		Location loc = e.getEntity().getLocation();
		world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 10);
		for (int i = 0; i < world.getEntities().size(); i++) {
			if (world.getEntities().get(i) instanceof LivingEntity) {
				LivingEntity entity = (LivingEntity) world.getEntities().get(i);
				if (Math.abs(entity.getLocation().distance(loc)) < 5) {
					entity.damage(4);
					new PotionEffect(PotionEffectType.SLOW, 60, 100, false, false, false).apply(entity);
				}
			}
		}
		
		e.getEntity().remove();
	}
	
	private void explosionEffect(ProjectileHitEvent e) {
		e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 5, false);
		e.getEntity().remove();
	}
	
	private void lightningEffect(ProjectileHitEvent e) {
		e.getEntity().getWorld().strikeLightning(e.getEntity().getLocation());
		e.getEntity().remove();
	}
}
