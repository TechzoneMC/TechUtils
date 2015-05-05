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

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.techcable.techutils.yamler.ConfigSection;
import net.techcable.techutils.yamler.InternalConverter;
import org.bukkit.Material;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ItemStack implements Converter {
    private InternalConverter converter;

    public ItemStack(InternalConverter converter) {
        this.converter = converter;
    }

    @Override
    public Object toConfig(Class<?> type, Object obj, ParameterizedType genericType) throws Exception {
        org.bukkit.inventory.ItemStack itemStack = (org.bukkit.inventory.ItemStack) obj;

        Map<String, Object> saveMap = new HashMap<>();
        saveMap.put("id", itemStack.getType() + ((itemStack.getDurability() > 0) ? ":" + itemStack.getDurability() : ""));
        saveMap.put("amount", itemStack.getAmount());

        Converter listConverter = converter.getConverter(List.class);

        Map<String, Object> meta = new HashMap<>();
        meta.put("name", itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : null);
        meta.put("lore", itemStack.getItemMeta().hasLore() ? listConverter.toConfig(List.class, itemStack.getItemMeta().getLore(), null) : null);

        saveMap.put("meta", meta);

        return saveMap;
    }

    @Override
    public Object fromConfig(Class type, Object section, ParameterizedType genericType) throws Exception {
        Map<String, Object> itemstackMap = (Map<String, Object>) ((ConfigSection) section).getRawMap();
        Map<String, Object> metaMap = (Map<String, Object>) ((ConfigSection) itemstackMap.get("meta")).getRawMap();

        String[] temp = ((String) itemstackMap.get("id")).split(":");
        org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(Material.valueOf(temp[0]));
        itemStack.setAmount((int) itemstackMap.get("amount"));

        if (temp.length == 2) {
            itemStack.setDurability(Short.valueOf(temp[1]));
        }

        if (metaMap.get("name") != null) {
            itemStack.getItemMeta().setDisplayName((String) metaMap.get("name"));
        }

        if (metaMap.get("lore") != null) {
            Converter listConverter = converter.getConverter(List.class);
            itemStack.getItemMeta().setLore((List<String>) listConverter.fromConfig(List.class, metaMap.get("lore"), null));
        }

        return itemStack;
    }

    @Override
    public boolean supports(Class<?> type) {
        return org.bukkit.inventory.ItemStack.class.isAssignableFrom(type);
    }

}
