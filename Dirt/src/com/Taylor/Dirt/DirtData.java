package com.Taylor.Dirt;

import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

public class DirtData {
	public Block dirt;
	public ArmorStand stand;
	public boolean following = false;
	public Entity follower;
	
	public DirtData(Block dirt, ArmorStand stand) {
		this.dirt = dirt;
		this.stand = stand;
	}
	
	public void follow(Entity follower) {
		this.following = true;
		this.follower = follower;
	}
	
	public void drop() {
		this.follower = null;
		this.following = false;
	}
}
