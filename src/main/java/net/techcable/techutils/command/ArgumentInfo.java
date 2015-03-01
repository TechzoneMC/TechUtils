package net.techcable.techutils.command;

import java.util.EnumSet;

import lombok.Getter;

public class ArgumentInfo<U extends Argument<?>> {
	
	public ArgumentInfo(String def, String name, ArgumentRequirement... requirements) {
		this.requirements = EnumSet.noneOf(ArgumentRequirement.class);
		this.def = def;
		this.name = name;
		for (ArgumentRequirement requirement : requirements) {
			this.requirements.add(requirement);
		}
	}
	
	public ArgumentInfo(String name, ArgumentRequirement... requirements) {
		this(null, name, requirements);
	}
	
	private final EnumSet<ArgumentRequirement> requirements;
	private final String def;
	@Getter
	private final String name;
	
	public boolean isValid(U argument) {
		for (ArgumentRequirement requirement : requirements) {
			switch (requirement) {
			case PLAYER :
				if (argument.getAsPlayerEntity() == null) return false;
				break;
			case BOOLEAN :
				try {
					argument.getAsBoolean();
					break;
				} catch (IllegalStateException e) {
					return false;
				}
			case NUMBER :
				try {
					argument.getAsDouble();
					break;
				} catch (IllegalStateException e) {
					return false;
				}
			case TIME :
				try {
					argument.getAsTime();
					break;
				} catch (IllegalStateException e) {
					return false;
				}
			}
		}
		return true;
	}
	
	public ArgumentRequirement getInvalidatedRequirement(U argument) {
		for (ArgumentRequirement requirement : requirements) {
			switch (requirement) {
			case PLAYER :
				if (argument.getAsPlayerEntity() == null) return ArgumentRequirement.PLAYER;
				break;
			case BOOLEAN :
				try {
					argument.getAsBoolean();
					break;
				} catch (IllegalStateException e) {
					return ArgumentRequirement.BOOLEAN;
				}
			case NUMBER :
				try {
					argument.getAsDouble();
					break;
				} catch (IllegalStateException e) {
					return ArgumentRequirement.NUMBER;
				}
			case TIME :
				try {
					argument.getAsTime();
					break;
				} catch (IllegalStateException e) {
					return ArgumentRequirement.TIME;
				}
			}
		}
		return null;
	}
	
	public String getInvalidatedRequirementMessage(U argument) {
		ArgumentRequirement requirement = getInvalidatedRequirement(argument);
		switch (requirement) {
		case PLAYER :
			return argument.getValue() + " isn't an online player";
		default :
			return argument.getValue() + " isn't a valid " + requirement.name();
		}
	}

	public static enum ArgumentRequirement {
		PLAYER,
		BOOLEAN,
		NUMBER,
		TIME;
	}
	
	public boolean isRequired() {
		return def != null;
	}
	
	public String getDefault() {
		return def;
	}
}