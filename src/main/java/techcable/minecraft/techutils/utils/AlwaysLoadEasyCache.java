package techcable.minecraft.techutils.utils;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class AlwaysLoadEasyCache<K, V> extends EasyCache<K, V> {
	private final Loader<K, V> loader;
	
	@Override
	public V get(K key) {
		return loader.load(key);
	}

}
