package net.techcable.techutils.ui;

import net.techcable.techutils.packet.PacketPlayOutTitle;
import net.techcable.techutils.packet.PacketPlayOutTitle.TitleAction;

import org.bukkit.entity.Player;

import lombok.*;

/**
* Represents a 1.8 title
*
* Supports Real 1.8 and Fake 1.8
*
* @author Techcable
*/
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Title {
	
	public Title(String title, String subtitle) {
		setTitle(title);
		setSubtitle(subtitle);
	}
	
	private String title;
	private String subtitle;
	private int fadeIn = 20;
	private int stay = 200;
	private int fadeOut = 20;

	/**
	* Display the players this title
	* Only shows to players who are on 1.8
	*
	* @param players players to display this title to
	*/ 
	public void sendTo(Player... players) {
		for (Player player : players) {
			PacketPlayOutTitle.create(TitleAction.RESET).sendTo(player);
			boolean shouldSend = false;
			if (title != null && !title.isEmpty()) {
				PacketPlayOutTitle.create(TitleAction.SET_TITLE, getTitle()).sendTo(player);
				shouldSend = true;
			}
			if (subtitle != null && !subtitle.isEmpty()) {
				PacketPlayOutTitle.create(TitleAction.SET_SUBTITLE, getSubtitle()).sendTo(player);
				shouldSend = false;
			}
			if (shouldSend) {
				PacketPlayOutTitle.create(TitleAction.DISPLAY, getFadeIn(), getStay(), getFadeOut()).sendTo(player);
			}
		}
	}
	
	public void hide(Player p) {
		PacketPlayOutTitle.create(TitleAction.HIDE).sendTo(p);
	}
	
	public static void unHide(Player p) {
		PacketPlayOutTitle.create(TitleAction.DISPLAY, 20, 100, 20);
	}
}