package techcable.minecraft.techutils;

import org.bukkit.util.Vector;

import lombok.*;

@Getter
public class VelocityUtils {
	
	private VelocityUtils() {}
	
	public static Vector knockback(Vector velocity, double power) {
		Vector knockback = velocity.clone();
		knockback.multiply(-power);
		knockback.setY(1);
		return knockback;
	}
}
