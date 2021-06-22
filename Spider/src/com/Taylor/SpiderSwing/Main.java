package com.Taylor.SpiderSwing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener, TabCompleter {
	private HashMap<Arrow, Player> players = new HashMap<Arrow, Player>();
	private HashMap<Arrow, Bat> bats = new HashMap<Arrow, Bat>();
	private HashSet<Player> inFlight = new HashSet<Player>();
	private ArrayList<String> arguments;
	private ItemStack grapplingHook;
	private NamespacedKey key;
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (Arrow arrow: bats.keySet())  {
					try {
						bats.get(arrow).teleport(arrow.getLocation().subtract(arrow.getVelocity().normalize()));
					} catch(IllegalArgumentException e) {
						bats.get(arrow).remove();
					}
				}
			}
		}, 0, 1);
		
		// Make Hook
		grapplingHook = new ItemStack(Material.IRON_PICKAXE);
		ItemMeta meta = grapplingHook.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Grappling Hook");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Left Click to Throw Grappling Hook");
		meta.setLore(lore);
		grapplingHook.setItemMeta(meta);
		
		
		// Make Recipe
		key = new NamespacedKey(this, "grappling_hook");
		
		ShapedRecipe recipe = new ShapedRecipe(key, grapplingHook);
		recipe.shape("  P", " L ", "L  ");
		recipe.setIngredient('P', Material.IRON_PICKAXE);
		recipe.setIngredient('L', Material.LEAD);
		Bukkit.addRecipe(recipe);
	}

	@Override
	public void onDisable() {
		players.clear();
		Bukkit.getScheduler().cancelTasks(this);
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (arguments == null) {
			arguments = new ArrayList<String>();
			arguments.add("start");
			arguments.add("stop");
		}
		ArrayList<String> res = new ArrayList<String>();
		if (args.length == 1) {
			for (String str: arguments) if(str.toLowerCase().startsWith(args[0].toLowerCase())) res.add(str);
			return res;
		}
		return null;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.getPlayer().discoverRecipe(key);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item != null && item.getType() == Material.IRON_PICKAXE) {
			if (item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Grappling Hook")) {
				e.setCancelled(true);
				if (e.getAction() == Action.LEFT_CLICK_AIR && player.rayTraceBlocks(100) != null) {
					Location loc = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(2.5));
					Arrow arrow = player.getWorld().spawnArrow(loc, player.getEyeLocation().getDirection(), 3, 0);
					arrow.setMetadata("nonDamagingArrow", new FixedMetadataValue(this, "noDamage"));
					arrow.setGravity(false);
					
					Bat bat = (Bat) player.getWorld().spawnEntity(player.getLocation().subtract(arrow.getVelocity().normalize()), EntityType.BAT);
					bat.setInvisible(true);
					bat.setLeashHolder(player);
					bat.setSilent(true);
					
					players.put(arrow, player);
					bats.put(arrow, bat);
					
					ItemMeta meta = item.getItemMeta();
					if (meta instanceof Damageable) {
						Damageable dmeta = (Damageable) meta;
						dmeta.setDamage(dmeta.getDamage() + 1);
						if (dmeta.getDamage() >= 250) {
							item.setAmount(0);
							player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
							player.playEffect(EntityEffect.BREAK_EQUIPMENT_MAIN_HAND);
						}
					}
					item.setItemMeta(meta);
					Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						@Override
						public void run() {
							arrow.remove();
							players.remove(arrow);
							if (bats.containsKey(arrow)) bats.get(arrow).remove();
							bats.remove(arrow);
						}
					}, 200);
				}
			}
		}
	}
	
	@EventHandler
	public void onHit(ProjectileHitEvent e) {
		if (e.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) e.getEntity();
			if (players.containsKey(arrow)) {
				if (e.getHitEntity() != null) {
					arrow.remove();
					players.remove(arrow);
					bats.get(arrow).remove();
					bats.remove(arrow);
					return;
				}
				Player player = players.get(arrow);
				double dx = arrow.getLocation().getX() - player.getLocation().getX();
				double dy = arrow.getLocation().getY() - player.getLocation().getY();
				double dz = arrow.getLocation().getZ() - player.getLocation().getZ();
				double dxz = Math.pow(dx * dx + dz * dz, 0.5);
				double vx = dx / dxz * 5;
				double vz = dz / dxz * 5;
				double g = -18 / (20 * 20);
				double t = (dxz / 5);
				double vy;
				if (dy / t - 1 / 2 * g * t - dxz / 3 <= 0) vy = dy / t - 0.5 * g  * t;
				else vy = Math.pow(dy / 2, 0.5);
				
				Vector velocity = new Vector(vx, vy, vz);
				player.setVelocity(velocity);
				arrow.remove();
				players.remove(arrow);
				bats.get(arrow).remove();
				bats.remove(arrow);
				inFlight.add(player);
			}
		}
	}
	
	@EventHandler
	public void onFall(EntityDamageEvent e) {
		if (e.getCause() == DamageCause.FALL && e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (inFlight.contains(player)) {
				inFlight.remove(player);
				e.setDamage(e.getDamage() / 2);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager().hasMetadata("nonDamagingArrow")) e.setCancelled(true);
	}
}
