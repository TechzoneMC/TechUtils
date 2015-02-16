package net.techcable.techutils.packet;

import java.lang.reflect.Method;

import static net.techcable.techutils.Reflection.*;

public class Converters {
	private Converters() {}
	
	public static Converter<String, Object[]> getIChatBaseComponentConverter() {
		return new Converter<String, Object[]>() {

			@Override
			public Object[] toNms(String chat) {
				if (chat == null) return null;
				Class<?> craftChatMessage = getCbClass("util.CraftChatMessage");
			    Method fromString = makeMethod(craftChatMessage, "fromString", String.class);
			    return callMethod(fromString, null, chat);
			}

			@Override
			public String toWrapper(Object[] nmsArray) {
				if (nmsArray == null) return null;
				if (nmsArray.length == 0) return "";
				StringBuilder output = new StringBuilder();
				for (Object nms : nmsArray) {
					output.append(toWrapper(nms));
				}
				return output.toString();
			}
			
			private String toWrapper(Object nms) {
				Class<?> craftChatMessage = getCbClass("util.CraftChatMessage");
			    Method fromString = makeMethod(craftChatMessage, "fromString", getNmsClass("IChatBaseComponent"));
			    return callMethod(fromString, null, nms);
			}
		};
	}
	
	public static interface Converter<T, U> {
		public U toNms(T wrapper);
		public T toWrapper(U nms);
	}
}
