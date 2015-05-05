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
					output.append(toWrapper0(nms));
				}
				return output.toString();
			}
			
			private String toWrapper0(Object nms) {
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
