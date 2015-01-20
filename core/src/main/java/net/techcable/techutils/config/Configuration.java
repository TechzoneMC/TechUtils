package net.techcbale.techutils.config;

public abstract class Configuration {
    private ConfigurationSerializer serializer;
    public Configuration(String resourceName, File configFile) {
        URL from = getClass().getResource(resourceName);
        this.serializer = new ConfigurationSerializer(from, configFile, this);
    }
    
    public File getFile() {
        return serializer.getFile();
    }
    
    public void save() {
        serializer.save();
    }
    
    public void load() {
        serializer.load();
    }
}