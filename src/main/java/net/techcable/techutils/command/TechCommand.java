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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.techcable.techutils.TechPlugin;
import net.techcable.techutils.entity.TechPlayer;

/**
 * Base of the TechUtils command framework
 * 
 * Supports subcommmands, 
 * 
 * @author Techcable
 *
 * @param <T>
 * @param <U>
 */
@RequiredArgsConstructor
@Getter
public abstract class TechCommand<T extends TechPlayer, U extends Argument<T>> implements CommandExecutor {
	private final TechPlugin<T> plugin;
	private List<String> aliases = new ArrayList<>();
	private List<TechCommand<T, U>> subCommands = new ArrayList<>();
	private Set<String> requiredPermissions = new HashSet<>();
	private List<ArgumentInfo<U>> argumentInfo = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		return onCommand(sender, label, Lists.newArrayList(args));
	}

	public boolean onCommand(CommandSender sender, String label, List<String> rawArgs) {
		if (!isAlias(label)) return false;
		if (rawArgs.size() > 0) {
			for (TechCommand<T, U> subCommand : subCommands) {
				if (subCommand.isAlias(rawArgs.get(0))) {
					rawArgs.remove(0);
					return subCommand.onCommand(sender, rawArgs.get(0), rawArgs);
				}
			}
		}
		List<U> args = new ArrayList<>();
		
		for (int i = 0; i < argumentInfo.size(); i++) {
			ArgumentInfo<U> argInfo = argumentInfo.get(i);
			String value;
			if (i <= rawArgs.size()) {
				if (argInfo.isRequired()) {
					sender.sendMessage("You must specifiy " + argInfo.getName());
					return true;
				} else value = argInfo.getDefault();
			} else {
				value = rawArgs.get(i);
			}
			U argument = createArgument(value);
			if (!argInfo.isValid(argument)) {
				sender.sendMessage(argInfo.getInvalidatedRequirementMessage(argument));
				return true;
			}
			args.add(argument);
		}
		return execute(sender, label, args);
	}
	
	public abstract boolean execute(CommandSender sender, String label, List<U> args);
	
	@SuppressWarnings("unchecked")
	protected U createArgument(String value) { //Must overide if custom args used
		return (U) new Argument<>(this, value);
	}
	
	private boolean isAlias(String other) {
		for (String label : aliases) {
			if (label.equalsIgnoreCase(other)) return true;
		}
		return false;
	}
}