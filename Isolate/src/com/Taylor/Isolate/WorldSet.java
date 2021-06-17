package com.Taylor.Isolate;

import org.bukkit.World;
import org.bukkit.World.Environment;

public class WorldSet {
	public World normal, nether, end;
	
	public WorldSet(World normal, World nether, World end) {
		this.end = end;
		this.nether = nether;
		this.normal = normal;
	}
	
	public World getWorld(Environment env) {
		switch (env) {
			case NORMAL: return normal;
			case NETHER: return nether;
			case THE_END: return end;
			default:
				break;
		}
		return null;
	}
}
