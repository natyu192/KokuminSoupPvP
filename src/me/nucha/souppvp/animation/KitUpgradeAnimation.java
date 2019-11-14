package me.nucha.souppvp.animation;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.souppvp.util.FireworkUtil;

public class KitUpgradeAnimation extends BukkitRunnable {

	private int count;
	private Location center;

	public KitUpgradeAnimation(Location center) {
		this.count = 9;
		this.center = center;
	}

	@Override
	public void run() {
		if (count == 0) {
			cancel();
			return;
		}
		if (count == 1) {
			FireworkUtil.explode(center, Color.LIME, true, true, Type.BALL_LARGE);
		}
		if (count > 1) {
			int angle = 360 / 8 * count;
			double x = Math.sin(angle * (Math.PI / 180)) * 2;
			double z = Math.cos(angle * (Math.PI / 180)) * 2;
			Location l = center.clone().add(x, 0, z);
			FireworkUtil.explode(l, Color.GREEN, false, false, Type.BALL);
		}
		count--;
	}

}
