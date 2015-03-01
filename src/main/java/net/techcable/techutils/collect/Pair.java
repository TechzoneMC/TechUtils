package net.techcable.techutils.collect;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class Pair<T, U> {
	private T first;
	private U second;
}
