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
package net.techcable.techutils.yamler;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class YamlConfigMapper extends ConfigBasic {
    private transient Yaml yaml;
    protected transient ConfigSection root;
    private transient Map<String, ArrayList<String>> comments = new LinkedHashMap<>();
    private transient Representer yamlRepresenter = new Representer();

    protected YamlConfigMapper() {
        DumperOptions yamlOptions = new DumperOptions();
        yamlOptions.setIndent(2);
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        yaml = new Yaml(new CustomClassLoaderConstructor(YamlConfigMapper.class.getClassLoader()), yamlRepresenter, yamlOptions);
    }

    protected void loadFromYaml() throws InvalidConfigurationException {
        root = new ConfigSection();

        try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(CONFIG_FILE), Charset.forName("UTF-8"))) {
            Object object = yaml.load(fileReader);

            if (object != null)
                convertMapsToSections((Map<?, ?>) object, root);
        } catch (IOException | ClassCastException | YAMLException e) {
            throw new InvalidConfigurationException("Could not load YML", e);
        }
    }

    private void convertMapsToSections(Map<?, ?> input, ConfigSection section) {
        if (input == null) return;

        for (Map.Entry<?, ?> entry : input.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();

            if (value instanceof Map) {
                convertMapsToSections((Map<?, ?>) value, section.create(key));
            } else {
                section.set(key, value);
            }
        }
    }

    protected void saveToYaml() throws InvalidConfigurationException {
        try (OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(CONFIG_FILE), Charset.forName("UTF-8"))) {
            if (CONFIG_HEADER != null) {
                for (String line : CONFIG_HEADER) {
                    fileWriter.write("# " + line + "\n");
                }

                fileWriter.write("\n");
            }

            Integer depth = 0;
            ArrayList<String> keyChain = new ArrayList<>();
            String yamlString = yaml.dump(root.getValues(true));
            StringBuilder writeLines = new StringBuilder();
            for (String line : yamlString.split("\n")) {
                if (line.startsWith(new String(new char[depth]).replace("\0", " "))) {
                    keyChain.add(line.split(":")[0].trim());
                    depth = depth + 2;
                } else {
                    if (line.startsWith(new String(new char[depth - 2]).replace("\0", " "))) {
                        keyChain.remove(keyChain.size() - 1);
                    } else {
                        //Check how much spaces are infront of the line
                        int spaces = 0;
                        for (int i = 0; i < line.length(); i++) {
                            if (line.charAt(i) == ' ') {
                                spaces++;
                            } else {
                                break;
                            }
                        }

                        depth = spaces;

                        if (spaces == 0) {
                            keyChain = new ArrayList<>();
                            depth = 2;
                        } else {
                            ArrayList<String> temp = new ArrayList<>();
                            int index = 0;
                            for (int i = 0; i < spaces; i = i + 2, index++) {
                                temp.add(keyChain.get(index));
                            }

                            keyChain = temp;

                            depth = depth + 2;
                        }
                    }

                    keyChain.add(line.split(":")[0].trim());
                }

                String search;
                if (keyChain.size() > 0) {
                    search = join(keyChain, ".");
                } else {
                    search = "";
                }


                if (comments.containsKey(search)) {
                    for (String comment : comments.get(search)) {
                        writeLines.append(new String(new char[depth - 2]).replace("\0", " "));
                        writeLines.append("# ");
                        writeLines.append(comment);
                        writeLines.append("\n");
                    }
                }

                writeLines.append(line);
                writeLines.append("\n");
            }

            fileWriter.write(writeLines.toString());
        } catch (IOException e) {
            throw new InvalidConfigurationException("Could not save YML", e);
        }
    }

    private static String join(List<String> list, String conjunction) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first)
                first = false;
            else
                sb.append(conjunction);
            sb.append(item);
        }

        return sb.toString();
    }

    public void addComment(String key, String value) {
        if (!comments.containsKey(key)) {
            comments.put(key, new ArrayList<String>());
        }

        comments.get(key).add(value);
    }

    public void clearComments() {
        comments.clear();
    }
}