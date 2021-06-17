package com.Taylor.Lockout;

import org.bukkit.advancement.Advancement;

public class AdvancementContainer {
	public Advancement advancement;
	public String title, description;
	public boolean completed = false;
	public int index;
	
	public AdvancementContainer(Advancement adv, String title, String des, int index) {
		this.advancement = adv;
		this.title = title;
		this.description = des;
		this.index = index;
	}
}
