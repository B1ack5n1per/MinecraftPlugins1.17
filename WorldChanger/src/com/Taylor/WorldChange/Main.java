package com.Taylor.WorldChange;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> res = new ArrayList<String>();
		if (args.length == 1) {
			res.add("generate");
			res.add("transport");
			return res;
		}
		if (args.length == 2) {
			if (args[0].equals("generate")) {
				res.add("normal");
				res.add("nether");
				res.add("end");
				return res;
			}
			if (args[0].equals("transport")) {
				List<World> worlds = Bukkit.getWorlds();
				for (World world : worlds) res.add(world.getName());
				return res;
			}
		}
		if (args.length == 3) {
			if (args[0].equals("generate")) {
				res.add("name");
				return res;
			}
			if (args[0].equals("transport")) {
				res.add("x y z");
				return res;
			}
		}
		return null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("changeWorld")) {
			if (args.length > 0) {
				switch (args[0]) {
					case "generate":
						if (args.length < 3) {
							sender.sendMessage(ChatColor.RED + "improper usage: /changeWorld generate <type> <name>");
							return true;
						}
						World.Environment env;
						switch(args[1]) {
							case "normal":
								env = World.Environment.NORMAL;
								break;
							case "nether":
								env = World.Environment.NETHER;
								break;
							case "end":
								env = World.Environment.THE_END;
							default:
								env = null;
						}
						String name = args[2];
						if (env != null) {
							new WorldCreator(name).environment(env).type(WorldType.NORMAL).createWorld();
							sender.sendMessage(ChatColor.GREEN + "World Generated Successfully");
							return true;
						} else {
							sender.sendMessage(ChatColor.RED + "improper usage: /changeWorld generate <type> <name>");
							return true;
						}
					case "transport":
						if (sender instanceof Player) {
							if (args.length < 5) {
								sender.sendMessage(ChatColor.RED + "improper usage: /changeWorld transport <name> <x> <y> <z>");
								return true;
							}
							
							World world = Bukkit.getWorld(args[1]);
							if (world == null) {
								sender.sendMessage(ChatColor.RED + "improper usage: /changeWorld transport <name> <x> <y> <z>");
								return true;
							}
							try {
								double x = Double.parseDouble(args[2]), y = Double.parseDouble(args[3]), z = Double.parseDouble(args[4]);
								((Player) sender).teleport(new Location(world, x, y, z));
								return true;
							} catch(NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + "improper usage: /changeWorld transport <name> <x> <y> <z>");
								return true;
							}
						} else {
							sender.sendMessage(ChatColor.RED + "improper usage: /changeWorld transport <name> <x> <y> <z>");
							return true;
						}
				}
			}
			sender.sendMessage(ChatColor.RED + "improper usage: /changeWorld <gernerate/transport> [settings]");
			return true;
		}
		return false;
	}
}
