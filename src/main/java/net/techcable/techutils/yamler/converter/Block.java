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
package net.techcable.techutils.yamler.converter;

import net.techcable.techutils.yamler.ConfigSection;
import net.techcable.techutils.yamler.InternalConverter;
import org.bukkit.*;
import org.bukkit.Location;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Block implements Converter {
    private InternalConverter converter;

    public Block(InternalConverter converter) {
        this.converter = converter;
    }

    @Override
    public Object toConfig(Class<?> type, Object obj, ParameterizedType genericType) throws Exception {
        org.bukkit.block.Block block = (org.bukkit.block.Block) obj;

        Converter locationConverter = converter.getConverter(org.bukkit.Location.class);
        Map<String, Object> saveMap = new HashMap<>();
        saveMap.put("id", block.getType() + ((block.getData() > 0) ? ":" + block.getData() : ""));
        saveMap.put("location", locationConverter.toConfig(org.bukkit.Location.class, block.getLocation(), null));

        return saveMap;
    }

    @Override
    public Object fromConfig(Class type, Object section, ParameterizedType genericType) throws Exception {
        Map<String, Object> blockMap = (Map<String, Object>) ((ConfigSection) section).getRawMap();
        Map<String, Object> locationMap = (Map<String, Object>) ((ConfigSection) blockMap.get("location")).getRawMap();

        Location location = new org.bukkit.Location(Bukkit.getWorld((String) locationMap.get("world")), (Double) locationMap.get("x"), (Double) locationMap.get("y"), (Double) locationMap.get("z"));
        org.bukkit.block.Block block = location.getBlock();

        String[] temp = ((String) blockMap.get("id")).split(":");
        block.setType(Material.valueOf(temp[0]));

        if (temp.length == 2) {
            block.setData(Byte.valueOf(temp[1]));
        }

        return block;
    }

    @Override
    public boolean supports(Class<?> type) {
        return org.bukkit.block.Block.class.isAssignableFrom(type);
    }

}
