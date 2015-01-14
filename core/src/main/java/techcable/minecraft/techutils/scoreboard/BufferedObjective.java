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
package techcable.minecraft.techutils.scoreboard;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import lombok.*;


@Getter
/**
 * 
 * @author Techcable
 * Stores an objective and resets it when updated 
 * Based on BufferedObjective in FactionsUUID
 *
 */
@RequiredArgsConstructor
public class BufferedObjective {
	private final String internalName;
	private final Scoreboard scoreboard;
	private final LinkedHashMap<String, Integer> text = new LinkedHashMap<>();
	private String displayName;
	private boolean needsUpdate = false;
	public void addText(String text, int score) {
		getText().put(text, score);
		needsUpdate = true;
	}
	public void clearText() {
		text.clear();
		needsUpdate = true;
	}
	public void addText(String text) {
		addText(text, 1);
	}
	public void addText(List<String> texts) {
		for (String text : texts) {
			addText(text);
		}
	}
	public void addText(LinkedHashMap<String, Integer> texts) {
		for (Entry<String, Integer> text : texts.entrySet()) {
			addText(text.getKey(), text.getValue());
		}
	}
	public void setVisible(boolean visible) {
		if (visible) {
			getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
		} else {
			getObjective().setDisplaySlot(null);
		}
	}
	public void setDisplayName(String name) {
		this.displayName = name;
		needsUpdate = true;
	}
	public String getDisplayName() {
		if (displayName == null) {
			return internalName;
		}
		return displayName;
	}
	
	public void update() {
		if (!needsUpdate) return;
		setVisible(false);
		getObjective().unregister(); //Get rid of the old one
		getObjective().setDisplayName(getDisplayName());
		for (Entry<String, Integer> text : getText().entrySet()) {
			Score score = getObjective().getScore(text.getKey());
			score.setScore(text.getValue());
		}
		setVisible(true);
	}
	
	public Objective getObjective() {
		Objective objective = scoreboard.getObjective(internalName);
		if (objective == null) {
			objective = scoreboard.registerNewObjective(internalName, "dummy");
		}
		return objective;
	}
}