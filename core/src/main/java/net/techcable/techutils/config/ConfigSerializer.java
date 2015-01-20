package net.techcable.techutils.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigSerializer {
    
    public ConfigSerializer(Object toSerialise, URL original, File configFile) {
        this.toSerialise = toSerialise;
        this.original = original;
        this.configFile = configFile;
        
        List<Field> rawFields = new ArrayList<>();
        for (Field field : toSerialise.getClass().getDeclaredFields()) {
            if (field.getAnnotation(ConfigOption.class) != null) fields.add(field);
        }
        this.fields = new ConfigField[rawFields.size()];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = new ConfigField(rawFields.get(i));
        }
    }
    
    private final Object toSerialise;
    private final URL original;
    @Getter
    private final File configFile;

    private YamlConfiguration config;
    private ConfigField[] fields;

    public void save() {
        if (!configFile.exists()) copy(original, configFile);
        if (config != null) config = new YamlConfiguration();
        for (ConfigField field : fields) {
            saveField(field);
        }
        try {
            config.save(configFile);
        } catch (Exception e) {}
    }

    public void load() {
        if (!configFile.exists()) copy(original, configFile);
        if (config != null) config = new YamlConfiguration();
        for (ConfigField field : fields) {
            loadField(field);
        }
        try {
            config.load(configFile);
        } catch (Exception e) {}
    }

    public void saveField(ConfigField field) {
        try {
            config.set(field.getName(), field.getValue(toSerialise));
        } catch (Exception e) {}
    }
    
    public void loadField(ConfigField field) {
        try {
            Object value = config.get(field.getName(), null);
            if (value == null) return;
            field.setValue(toSerialise, value);
        } catch (Exception e) {}
    }
    
    public static void copy(URL from, File to) {
        BufferedReader in = null;
        BufferedWriter out = null;
        try {
            to.createNewFile();
            char[] buffer = new char[100];
            int i = 0;
            in = new BufferedReader(new InputStreamReader(in.openStream()));
            out = new BufferedWriter(new FileWriter(to));
            while(true) {
                int num = in.read(buffer);
                if (num < 1) break;
                out.write(buffer, 0, num);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
    }
}