package com.Taylor.TargetTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin {
	
	public HashMap<EnderDragon, Location> dragons = new HashMap<EnderDragon, Location>();
	
	@Override
	public void onEnable() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (EnderDragon dragon : dragons.keySet()) {
					double speed = dragon.getVelocity().length();
					Location target = dragons.get(dragon);
					double dx, dy, dz;
					dx = target.getX() - dragon.getLocation().getX();
					dy = target.getY() - dragon.getLocation().getY();
					dz = target.getZ() - dragon.getLocation().getZ();
					Vector dir = new Vector(dx, dy, dz).normalize();
					if (speed == 0) speed = 5;
					if (dir.length() > 0) dragon.setVelocity(dir.multiply(speed != 0 ? speed : 5));
				}
			}
		}, 1, 1);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> res = new ArrayList<String>();
		
		if (args.length == 1) {
			res.add("spawn");
			res.add("target");
		}
		
		return res;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("dragon")) {
			if (args.length > 0) {
				if (args[0].equals("spawn")) {
					dragons.put(player.getLocation().getWorld().spawn(((Player) sender).getLocation(), EnderDragon.class), player.getLocation());
				} else if (args[0].equals("target")) {
					for (EnderDragon dragon : dragons.keySet()) {
						dragons.put(dragon, player.getLocation());
					}
				}
			}
			return true;
		}
		
		return false;
	}
}
