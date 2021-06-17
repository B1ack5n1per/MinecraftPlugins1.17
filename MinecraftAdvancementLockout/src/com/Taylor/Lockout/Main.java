package com.Taylor.Lockout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class Main extends JavaPlugin implements Listener {
	private Inventory menu, colorSelect;
	private HashMap<Player, Color> playerMap;
	private boolean playing = false;
	private HashSet<Player> immune = new HashSet<Player>();
	private AdvancementContainer[] board = new AdvancementContainer[25];
	private String dir;
	private JSONObject data;
	
	@Override
	public void onEnable() {
		try {
			dir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath().replace("Lockout.jar", "lang.json");
		} catch (URISyntaxException e) {
			dir = "";
		}
		if (new File(dir).exists()) {
			JSONParser parser = new JSONParser();
			try {
				data = (JSONObject) parser.parse(new FileReader(new File(dir)));
				this.getServer().getPluginManager().registerEvents(this, this);
				
			} catch (Exception e) {
				System.out.println("error loading lang.json file: disabling plugin");
				this.getServer().getPluginManager().disablePlugin(this);
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	private void fillMenu() {
		menu = Bukkit.createInventory(null, 45, "Lockout");
		
		// Fill Menus
		colorSelect = Bukkit.createInventory(null, 9, "Select Color");
		for (Color col : Color.values()) colorSelect.addItem(Color.toItem(col));
		Iterator<Advancement> iter = Bukkit.advancementIterator();
		ArrayList<Advancement> advs = new ArrayList<Advancement>();
		while (iter.hasNext()) {
			Advancement adv = iter.next();
			if (!adv.getKey().getKey().contains("recipes")) advs.add(adv);
		}
		for (int i = 0; i < board.length; i++) {
			int index = (int) Math.floor(Math.random() * advs.size());
			int loc = (int) (i + 2 + 4 * Math.floor(i / 5));
			
			String key = advs.get(index).getKey().getKey();					
			String title = (String) data.get("advancements." + key.replaceAll("/",".") + ".title");
			String des = (String) data.get("advancements." + key.replaceAll("/",".") + ".description");
			board[i] = new AdvancementContainer(advs.get(index), title, des, loc);
			advs.remove(index);
			if (title == null) {
				i--;
				continue;
			}
			
			System.out.println(title);
			System.out.println(des);
			
			ItemStack logo = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
			ItemMeta logoMeta = logo.getItemMeta();
			logoMeta.setDisplayName(title);
			List<String> lore = new ArrayList<String>();
			lore.add(des);
			logoMeta.setLore(lore);
			logo.setItemMeta(logoMeta);
			menu.setItem(loc, logo);
		}
	}
	
	@EventHandler
	public void onAdvanec(PlayerAdvancementDoneEvent e) {
		if (playerMap.containsKey(e.getPlayer())) {
			boolean onBoard = false;
			AdvancementContainer ad = null;
			for (AdvancementContainer adv : board) {
				if (e.getAdvancement().equals(adv.advancement)) {
					onBoard = true;
					ad = adv;
					break;
				}
			}
			if (onBoard) {
				if (!ad.completed) {
					ad.completed = true;
					ItemStack newItem = menu.getItem(ad.index);
					newItem.setType(Color.toPane(playerMap.get(e.getPlayer())));
					menu.setItem(ad.index, newItem);
				}
			}
		}
	}
	
	@EventHandler
	public void onFall(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (immune.contains(player)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory().equals(menu)) e.setCancelled(true);
		else if (e.getInventory().equals(colorSelect)) {
			ItemStack item = e.getCurrentItem();
			if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Claim")) {
				Color selection = Color.fromItem(e.getCurrentItem());
				System.out.println(selection);
				colorSelect.remove(e.getCurrentItem());
				e.getWhoClicked().closeInventory();
				e.setCancelled(true);
				playerMap.put((Player) e.getWhoClicked(), selection);
				if (playerMap.keySet().size() == Color.values().length || playerMap.keySet().size() == this.getServer().getOnlinePlayers().size()) {
					int players = playerMap.keySet().size();
					double angle = Math.PI * 2 / players;
					double x = Math.floor(Math.random() * 100000 - 50000), y = 200, z = Math.floor(Math.random() * 100000 - 50000);
					int i = 0;
					immune = new HashSet<Player>();
					for (Player player : playerMap.keySet()) {
						double r = players < 2 ? 10000 : 10000 / (Math.sqrt(2 * (1 - Math.cos(angle))));
						double dx = r * Math.cos(angle * i), dz = r * Math.sin(angle * i);
						player.teleport(new Location(Bukkit.getWorld("world"), x + dx, y, z + dz));
						immune.add(player);
						Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
							@Override
							public void run() {
								immune.remove(player);
							}
						}, 200);
						i++;
					}
				}
			}
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> res = new ArrayList<String>();
		if (args.length == 1) {
			res.add("start");
			res.add("reset");
			res.add("stop");
			res.add("menu");
		}
		return res;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("lockout")) {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "improper usage: /lockout <start/reset/stop/menu>");
				return false;
			}
			
			if (args[0].equals("menu")) {
				if (sender instanceof Player && playing) ((Player) sender).openInventory(menu);
				return true;
			}
			
			if (args[0].equals("start")) {
				if (playing) {
					sender.sendMessage(ChatColor.RED + "a game is currently in progress");
					return true;
				}

				playerMap = new HashMap<Player, Color>();
				fillMenu();
				playing = true;
				for (Player player : this.getServer().getOnlinePlayers()) {
					Bukkit.dispatchCommand(sender, "advancement revoke @a everything");
					player.openInventory(colorSelect);
					player.sendMessage(ChatColor.GREEN + "Advancement Lockout started!\nType '/lockout menu' at any time to see the board!");
				}
				return true;
			}
		}
		return false;
	}
}
