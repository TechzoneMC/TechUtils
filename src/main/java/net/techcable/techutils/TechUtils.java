package net.techcable.techutils;

import org.bukkit.Bukkit;

public class TechUtils {
    public static void assertMainThread() {
        if (!Bukkit.isPrimaryThread()) {
            throw new UnsupportedOperationException("Asynchronous access is unsupported");
        }
    }
}
