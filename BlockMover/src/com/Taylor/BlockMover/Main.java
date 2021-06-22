package com.Taylor.BlockMover;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;

public class Main extends JavaPlugin implements Listener {
	private HashMap<Player, ArmorStand> players = new HashMap<Player, ArmorStand>();
	private HashMap<Player, Integer> delays = new HashMap<Player, Integer>();
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (Player player: players.keySet()) {
					ArmorStand stand = players.get(player);
					stand.teleport(player.getLocation().add(player.getEyeLocation().getDirection().multiply(2)));
				}

				for (Player player: delays.keySet()) {
					delays.put(player, delays.get(player) - 1);
					if (delays.get(player) == 0) delays.remove(player);
				}
			}
		}, 0, 1);
	}
	
	@Override
	public void onDisable() {
	
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("wand")) {
			if (sender instanceof Player) {
				ItemStack wand = new ItemStack(Material.STICK);
				ItemMeta meta = wand.getItemMeta();
				meta.setDisplayName(ChatColor.DARK_PURPLE + "Wand");
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(ChatColor.BLUE + "" + ChatColor.ITALIC + "Right click a block to move it");
				meta.setLore(lore);
				wand.setItemMeta(meta);
				
				((Player) sender).getInventory().addItem(wand);
			}
		}
		return false;
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getItem() != null && e.getItem().getType() == Material.STICK) {
				if (e.getItem().getItemMeta().getLore() != null && e.getItem().getItemMeta().getLore().get(0).equals(ChatColor.BLUE + "" + ChatColor.ITALIC + "Right click a block to move it")) {
					if (!delays.containsKey(e.getPlayer())) {
						if (!players.containsKey(e.getPlayer())) {
							Location loc = e.getClickedBlock().getLocation();
							ArmorStand stand = (ArmorStand) e.getPlayer().getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
							stand.setInvisible(true);
							stand.getEquipment().setHelmet(new ItemStack(e.getClickedBlock().getType()));
							stand.setSmall(true);
							stand.setGravity(false);
							stand.setInvulnerable(true);
							e.getPlayer().getWorld().getBlockAt(loc).setType(Material.AIR);
							players.put(e.getPlayer(), stand);
							delays.put(e.getPlayer(), 5);
						} else {
							ArmorStand stand = players.get(e.getPlayer());
							players.remove(e.getPlayer());
							RayTraceResult ray = e.getPlayer().rayTraceBlocks(5, FluidCollisionMode.NEVER);
							Location loc = ray.getHitBlock().getLocation().add(ray.getHitBlockFace().getDirection()).subtract(0, 1, 0);
							stand.getWorld().getBlockAt(loc.add(0, 1, 0)).setType(stand.getEquipment().getHelmet().getType());
							stand.remove();
							delays.put(e.getPlayer(), 5);
						}
					}
					e.setCancelled(true);
				}
			}
		}
	}
}
