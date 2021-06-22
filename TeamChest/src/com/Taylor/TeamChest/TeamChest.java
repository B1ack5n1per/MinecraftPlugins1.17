package com.Taylor.TeamChest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class TeamChest {
	public Team team;
	public Location loc;
	
	public TeamChest(Team team, Location loc) {
		this.team = team;
		this.loc = loc;
	}
	
	public TeamChest(JSONObject obj) {
		for (Team team : Main.teams) if (team.name.equals((String) obj.get("team"))) this.team = team;
		JSONObject loc = (JSONObject) obj.get("loc");
		this.loc = new Location(Bukkit.getWorld((String) loc.get("world")), (Double) loc.get("x"), (Double) loc.get("y"), (Double) loc.get("z"));
	}
	
	public boolean open(Player player) {
		System.out.println(team.players);
		if (team.players.contains(player.getDisplayName())) {
			player.openInventory(team.inv);
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		
		obj.put("team", team.name);
		obj.put("loc", locToJSONObject(loc));
		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject locToJSONObject(Location loc) {
		JSONObject obj = new JSONObject();
		
		obj.put("world", loc.getWorld().getName());
		obj.put("x", loc.getX());
		obj.put("y", loc.getY());
		obj.put("z", loc.getZ());
		
		return obj;
	}
}
