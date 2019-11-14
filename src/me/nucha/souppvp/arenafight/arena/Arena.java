package me.nucha.souppvp.arenafight.arena;

import org.bukkit.Location;

import me.nucha.souppvp.kit.Kit;
import me.nucha.souppvp.kit.KitManager;

public class Arena {

	private String id;
	private String name;
	private Location spawn1;
	private Location spawn2;
	private Kit kit;
	private boolean enabled;

	public Arena(String id, String name) {
		this(id, name, null, null);
	}

	public Arena(String id, String name, Location spawn1, Location spawn2) {
		this(id, name, spawn1, spawn2, KitManager.getKits().get(0), false);
	}

	public Arena(String id, String name, Location spawn1, Location spawn2, Kit kit, boolean enabled) {
		this.id = id;
		this.name = name;
		this.spawn1 = spawn1;
		this.spawn2 = spawn2;
		this.kit = kit;
		this.enabled = enabled;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Location getSpawn1() {
		return spawn1;
	}

	public Location getSpawn2() {
		return spawn2;
	}

	public Kit getKit() {
		return kit;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isAvailable() {
		if (enabled) {
			return (spawn1 != null) && (spawn2 != null) && (kit != null);
		}
		return false;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSpawn1(Location spawn1) {
		this.spawn1 = spawn1;
	}

	public void setSpawn2(Location spawn2) {
		this.spawn2 = spawn2;
	}

	public void setKit(Kit kit) {
		this.kit = kit;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
