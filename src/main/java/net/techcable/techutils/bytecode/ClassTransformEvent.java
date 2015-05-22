package net.techcable.techutils.bytecode;

import com.google.common.annotations.Beta;
import javassist.CtClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Beta
@Getter
public class ClassTransformEvent extends Event {
    private final CtClass clazz;
    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private ClassTransformEvent(boolean async, CtClass clazz) {
        super(async);
        this.clazz = clazz;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static void fire(CtClass clazz) {
        boolean async = !Bukkit.isPrimaryThread();
        ClassTransformEvent event = new ClassTransformEvent(async, clazz);
        Bukkit.getPluginManager().callEvent(event);
    }
}
