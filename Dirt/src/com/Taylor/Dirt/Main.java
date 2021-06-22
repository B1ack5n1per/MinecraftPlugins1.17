package com.Taylor.Dirt;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.entity.EntityDismountEvent;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {
	private HashMap<Player, DirtData> dirts = new HashMap<Player, DirtData>();
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (Player player: dirts.keySet()) {
					DirtData dirt = dirts.get(player);
					if (dirt.following) {
						dirt.stand.removePassenger(player);
						dirt.stand.teleport(dirt.follower.getLocation().add(dirt.follower.getLocation().getDirection().normalize()).subtract(0, 1, 0));
						dirt.stand.setPassenger(player);
						System.out.println("following");
					}
				}
			}
		}, 0, 1);
	}

	@Override
	public void onDisable() {
		for (Player player: dirts.keySet()) player.setInvisible(false);
		Bukkit.getScheduler().cancelTasks(this);
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dirt")) {
			if (args.length == 1) {
				for (Player player: Bukkit.getOnlinePlayers()) {
					if (player.getName().equals(args[0])) {
						Block block = player.getWorld().getBlockAt(player.getLocation());
						block.setType(Material.DIRT);
						block.setMetadata("playerBlock", new FixedMetadataValue(this, player));
						
						ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().getBlock().getLocation().subtract(-0.5, 1.25, -0.5), EntityType.ARMOR_STAND);
						stand.setInvisible(true);
						stand.setPassenger(player);
						stand.setGravity(false);
						dirts.put(player, new DirtData(block, stand));
						return true;
					}
				}
			}
			if (sender instanceof Player) {
				Player player = (Player) sender;
				for (DirtData data: dirts.values()) data.follow(player.getWorld().spawnEntity(player.getLocation().add(1, 1, 0), EntityType.ZOMBIE));
			}
			sender.sendMessage(ChatColor.RED + "Improper useage: dirt <player>");
		}
		
		return false;

	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onLeave(EntityDismountEvent e) {
		if (e.getEntity() instanceof Player) {
			if (dirts.containsKey((Player) e.getEntity())) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					@Override
					public void run() {
						e.getDismounted().setPassenger(e.getEntity());
					}
				}, 5);
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (dirts.containsKey(e.getPlayer())) e.setCancelled(true);
		if (e.getBlock().hasMetadata("playerBlock")) {
			Player player = (Player) e.getBlock().getMetadata("playerBlock");
			dirts.get(player).follow(e.getPlayer());
			
			ItemStack item = new ItemStack(Material.DIRT);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.of("#634210") + player.getName());
			item.setItemMeta(meta);
			

			e.setDropItems(false);
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (dirts.containsKey(e.getPlayer())) e.setCancelled(true);
		if (e.getItemInHand().getItemMeta().getDisplayName().length() > 0) {
			for (Player player: dirts.keySet()) {
				if (player.getName().equals(e.getItemInHand().getItemMeta().getDisplayName())) {
					e.getBlockPlaced().setMetadata("playerBlock", new FixedMetadataValue(this, player));
					dirts.get(player).drop();
				}
			}
		}
	}
	
	@EventHandler
	public void die(EntityDeathEvent e) {
		for (DirtData data: dirts.values()) {
			if (data.follower == e.getEntity()) {
				data.drop();
			}
		}
	}
}
