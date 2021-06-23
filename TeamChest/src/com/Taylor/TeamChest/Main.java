package com.Taylor.TeamChest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {
	public static HashSet<Team> teams;
	public static HashSet<TeamChest> chests;
	public static File data, root, backup;
	public static ItemStack teamChest;
	
	public void genDefaultJSON() {
		try {
			PrintWriter pw = new PrintWriter(data);
			pw.print("{\"teams\":[],\"chests\":[]}");
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		String path = System.getProperty("user.dir") + "\\plugins\\teamChestData";
		root = new File(path);
		data = new File(path + "\\data.json");
		backup = new File(path + "\\backup.json");
		
		// Check data file
		if (!data.exists()) {
			if(root.mkdirs()) {
				System.out.println("TeamChest: Data directory not found so new directory created");
				genDefaultJSON();
			}
		}

		chests = new HashSet<TeamChest>();
		teams = new HashSet<Team>();
		
		try {
			// Get plain text data
			String rawData = "";
			Scanner in = new Scanner(data);
			while(in.hasNext()) rawData += in.next();
			in.close();
			
			// Parse plain text to JSON
			JSONParser parser = new JSONParser();
			JSONObject parsedData = (JSONObject) parser.parse(rawData);
			
			JSONArray jchests = (JSONArray) parsedData.get("chests");
			JSONArray jteams = (JSONArray) parsedData.get("teams");

			// Use JSON to create teams
			for (Object obj : jteams) teams.add(new Team((JSONObject) obj));

			// Use JSON to create chests
			for (Object obj : jchests) chests.add(new TeamChest((JSONObject) obj));
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			genDefaultJSON();
			e.printStackTrace();
		}
		
		// Add default team
		if (teams.size() == 0) teams.add(new Team("default"));
		
		// Generate recipe
		teamChest = new ItemStack(Material.ENDER_CHEST);
		
		ItemMeta meta = teamChest.getItemMeta();
		meta.setDisplayName(ChatColor.of("#fc6b03") + "Team Chest");
		
		teamChest.setItemMeta(meta);
		
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "teamChest"), teamChest.clone());
		recipe.shape("ooo", "obo", "oeo");
		recipe.setIngredient('o', Material.OBSIDIAN);
		recipe.setIngredient('e', Material.ENDER_PEARL);
		recipe.setIngredient('b', Material.BLAZE_POWDER);
		
		Bukkit.addRecipe(recipe);
	}
	
	@Override
	public void onDisable() {
		save(data);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (chests.size() < 1) return;
		for (TeamChest chest : chests) {
			if (chest.loc.equals(e.getBlock().getLocation())) {
				for (Team team : teams) {
					if (team.players.contains(e.getPlayer().getDisplayName())) {
						chests.remove(chest);
						e.setDropItems(false);
						e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), teamChest.clone());
						return;
					}
				}
				e.getPlayer().sendMessage(ChatColor.RED + "This chest belongs to another team");
				e.setCancelled(true);
				return;
			}
		}
		
	}
	
	
	@EventHandler
	public void onPlace(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			// Check if team chest activated
			for (TeamChest chest : chests) {
				if (chest.loc.equals(e.getClickedBlock().getLocation())) {
					if (chest.open(e.getPlayer())) e.setCancelled(true);
					else {
						e.getPlayer().sendMessage(ChatColor.RED + "This chest belongs to another team");
						e.setCancelled(true);
					}
					return;
				}
			}
			
			// Check if placing a team chest
			if (e.getItem() != null && e.getItem().hasItemMeta()) {
				ItemMeta meta = e.getItem().getItemMeta();
				if (meta.hasDisplayName() && meta.getDisplayName().toLowerCase().equals((ChatColor.of("#fc6b03") + "Team Chest").toLowerCase())) {
					// Determine team to associate with chest
					for (Team team : teams) {
						if (team.players.contains(e.getPlayer().getDisplayName())) {
							chests.add(new TeamChest(team, e.getClickedBlock().getLocation().add(e.getBlockFace().getDirection().normalize())));
							return;
						}
					}
					e.getPlayer().sendMessage(ChatColor.RED + "Sorry you are not part of a team. Join a team and try again");
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> options = new ArrayList<String>();
		
		if (args.length == 1) {
			for (Team team : teams) options.add(team.name);
			options.add("backup");
			options.add("teams");
		}
		else if (args.length == 2) {
			options.add("add");
			options.add("remove");
			options.add("create");
		} else if (args.length == 3)  {
			if (args[1].equals("create"));
			else for (Player player : this.getServer().getOnlinePlayers()) options.add(player.getDisplayName());
		}
		return options;
	}
	
	@SuppressWarnings("unchecked")
	private boolean save(File dest) {
		// Serialize teams and chests
		JSONArray jteams = new JSONArray();
		for (Team team : teams) jteams.add(team.toJSONObject());
		
		JSONArray jchests = new JSONArray();
		for (TeamChest chest : chests) jchests.add(chest.toJSONObject());
		
		JSONObject out = new JSONObject();
		out.put("teams", jteams);
		out.put("chests", jchests);
		
		try {
			// Print to data file
			if (!dest.exists()) root.mkdirs();
			PrintWriter pw = new PrintWriter(dest);
			pw.print(out.toJSONString());
			pw.close();
			return true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.isOp()) {
			if (cmd.getName().equalsIgnoreCase("teamChest")) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("backup")) {
						if (save(backup)) sender.sendMessage(ChatColor.GREEN + "Backup Saved Successfully");
						return true;
					}
					if (args[0].equalsIgnoreCase("teams")) {
						sender.sendMessage(teams.toString());
						return true;
					}
				}
				if (args.length == 2 && args[1].equals("create")) {
					teams.add(new Team(args[0]));
					sender.sendMessage(ChatColor.GREEN + "Team " + args[0] + " created");
					return true;
				}
				if (args.length == 3) {
					if (!(sender instanceof Player)) return true;
					
					if (args[1].equals("add")) {
						for (Team team : teams)  {
							team.remove(args[2]);
							if (team.name.equals(args[0]) && this.getServer().getOnlinePlayers().contains(this.getServer().getPlayer(args[2]))) {
								team.add(args[2]);
								sender.sendMessage(ChatColor.GREEN + args[2] + " was successfully added to team " + team.name);
							}
						}
					}
	
					if (args[1].equals("remove")) {
						for (Team team : teams) {
							if (team.players.contains(args[2])) {
								team.remove(args[2]);
								sender.sendMessage(ChatColor.GREEN + args[2] + " was successfully removed from team " + team.name);
							}
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "improper usage: /teamChest <[team]/backup/teams> <add/remove/create> <player> [color]");
					return true;
				}
			}
		}
		return false;
	}
}
