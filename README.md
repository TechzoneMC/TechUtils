TechUtils
=========

Provides various utitlities for plugins:
- Loading of offline players
- UUID lookup

TODO: (in order of priority)
- [x] Javadocs
- [ ] Easy config api
- [ ] Easy scoreboard api
- [ ] Anotation based command system
- [x] Reflection based loading of offline players


##Usage
TechUtils is centered around TechPlugin and TechPlayer
ExamplePlugin:
````java
public class ExamplePlugin extends TechPlugin<ExamplePlayer> {
    
    @Override
    public void startup() {
        System.out.println("Startup");
    }
    
    @Override
    public void shutdown() {
        System.out.println("Shutdown");
    }
    
    @Override
    public ExamplePlayer createPlayer(UUID id) {
        return new ExamplePlayer(id, this);
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

## Credits
- Techcable - wrote it
- Akkarin - config format and parser
  - Making me convert to java 8