TechUtils
=========

Provides various utitlities for plugins:
- Loading of offline players
- Easy config api
- UUID lookup

TODO: (in order of priority)
- [ ] Javadocs
- [ ] Easy scoreboard api
- [ ] Anotation based command system
- [ ] Reflection based loading of offline players


##Usage
TechUtils is centered around TechPlugin and TechPlayer
ExamplePlugin:
````java
public class ExamplePlugin extends TechPlugin<ExamplePlayer> {

    @Override
    public TechPlayerFactory<ExamplePlayer> getPlayerFactory() {
        return new TechPlayerFactory<ExamplePlayer>() {
            @Override
            public T createPlayer(UUID id, TechPlugin<T> plugin) {
                return new ExamplePlayer(id, (ExamplePlugin)plugin);
            }
        }
    }

}
````
ExamplePlayer:
````java
public class ExamplePlayer extends TechPlayer {
    public boolean isAwesome() {
        return true;
    }
}
````
