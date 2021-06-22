package com.Taylor.TeamChest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class Team {
	public String name;
	public HashSet<String> players;
	public Inventory inv;
	
	public Team(String name) {
		this.name = name;
		this.players = new HashSet<String>();
		this.inv = Bukkit.createInventory(null, 27, name + " Team Chest");
	}
	
	public Team(JSONObject obj) {
		if (obj == null) return;
		this.name = (String) obj.get("name");
		this.players = new HashSet<String>();
		for (Object player : (JSONArray) obj.get("players")) players.add((String) player);
		this.inv = invDeserialize((String) obj.get("inv"));
	}
	
	public void add(String player) {
		players.add(player);
	}
	
	public void remove(String player) {
		players.remove(player);
	}
	
	public boolean isOnTeam(Player player) {
		return players.contains(player.getDisplayName());
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		
		
		JSONArray arr = new JSONArray();
		for (String player : players) arr.add(player);
		
		obj.put("players", arr);
		obj.put("name", name);
		obj.put("inv", invSerialize(inv));
		
		
		return obj;
	}
	
	private String invSerialize(Inventory inv) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
	        
	        for (int i = 0; i < inv.getSize(); i++) dataOutput.writeObject(inv.getItem(i));
	        
	        dataOutput.close();
			
	        return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch(IOException e) {
			e.printStackTrace();
		}
       return null;
	}
	
	private Inventory invDeserialize(String in) {
		Inventory inv = Bukkit.createInventory(null, 27, name + " Team Chest");
		if (in.length() > 0) {
			try {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(in));
				BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
		       
				for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, (ItemStack) dataInput.readObject());
				
		        dataInput.close();
			} catch(IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return inv;
	}
	
	@Override
	public String toString() {
		String res = "{ name: \"" + name + "\", players: [";
		for (String player : players) res += player + ", ";
		res += " }";
		
		
		return res;
	}
}
