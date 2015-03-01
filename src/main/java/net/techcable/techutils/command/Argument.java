package net.techcable.techutils.command;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.techcable.techutils.TechPlugin;
import net.techcable.techutils.entity.TechPlayer;
import net.techcable.techutils.entity.UUIDUtils;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents an argument to a command
 * 
 * @author Techcable
 *
 * @param <T> the type of player used in the plugin
 */
@RequiredArgsConstructor
@Getter
public class Argument<T extends TechPlayer> {
	private final TechCommand<T, ?> command;
	private final String value;
	
	/**
	 * Get the value of the arugment as a player entity
	 * 
	 * @return this argument as a player entity, or null if this isn't a valid player
	 */
	public Player getAsPlayerEntity() {
		return UUIDUtils.getPlayerExact(getValue());
	}
	
	/**
	 * Get the value of the arugment as a techplayer
	 * 
	 * @return this argument as a techplayer, or null if this isn't a valid player
	 */
	public T getAsPlayer() {
		return command.getPlugin().getPlayer(getAsPlayerEntity());
	}
	
	/**
	 * Get the value of the arugment as a boolean
	 * 
	 * @return this argument as a boolean
	 * @throws IllegalStateException if this isn't a valid boolean
	 */
	public boolean getAsBoolean() {
		if ("true".equalsIgnoreCase(getValue())) return true;
		if ("false".equalsIgnoreCase(getValue())) return false;
		throw new IllegalStateException("Not a valid boolean");
	}
	
	/**
	 * Get the value of the arugment as a long
	 * 
	 * @return this argument as a long
	 * @throws IllegalStateException if this isn't a valid long
	 */
	public long getAsLong() {
		try {
			return Long.parseLong(getValue());
		} catch (NumberFormatException e) {
			throw new IllegalStateException("Not a valid long", e);
		}
	}
	
	/**
	 * Get the value of the arugment as a int
	 * 
	 * @return this argument as a integer
	 * @throws IllegalStateException if this isn't a valid int
	 */
	public int getAsInteger() {
		try {
			return Integer.parseInt(getValue());
		} catch (NumberFormatException e) {
			throw new IllegalStateException("Not a valid int", e);
		}
	}
	
	/**
	 * Get the value of the arugment as a double
	 * 
	 * @return this argument as a double
	 * @throws IllegalStateException if this isn't a valid double
	 */
	public double getAsDouble() {
		try {
			return Double.parseDouble(getValue());
		} catch (NumberFormatException e) {
			throw new IllegalStateException("Not a valid int", e);
		}
	}
	
	public static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)(\\w+)");
	/**
	 * Get the value of the arugment as time in milliseconds
	 * 
	 * @return this argument as time in milleseconds
	 * @throws IllegalStateException if this isn't a valid time
	 */
	public long getAsTime() {
		long ms = 0;
		Matcher m = TIME_PATTERN.matcher(getValue());
		if (!m.find()) throw new IllegalStateException(getValue() + " doesn't match the time pattern");
		m.reset();
		while (m.find()) {
			long value = Long.valueOf(m.group(1));
			TimeUnit unit = fromName(m.group(2));
			if (unit == null) throw new IllegalStateException(m.group(2) + " isn't a valid time unit");
			ms += unit.toMillis(value);
		}
		return ms;
	}
	
	private static TimeUnit fromName(String name) {
		name = name.toLowerCase();
		for (TimeUnit value : TimeUnit.values()) {
			String nameOfUnit = value.name().toLowerCase();
			if (name.startsWith(nameOfUnit.substring(0, 1))) return value;
			if (name.equals(nameOfUnit)) return value;
			if (name.equals(nameOfUnit.substring(0, nameOfUnit.length() - 1))) return value;
		}
		return null;
	}
}