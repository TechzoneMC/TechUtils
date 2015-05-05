/**
 * The MIT License
 * Copyright (c) 2014-2015 Techcable
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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