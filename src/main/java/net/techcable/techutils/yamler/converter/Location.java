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
import java.util.Map;
import net.techcable.techutils.yamler.ConfigSection;
import net.techcable.techutils.yamler.InternalConverter;
import org.bukkit.Bukkit;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Location implements Converter {
    public Location(InternalConverter converter ) {
    }

    @Override
    public Object toConfig( Class<?> type, Object obj, ParameterizedType genericType ) throws Exception {
        org.bukkit.Location location = (org.bukkit.Location) obj;
        Map<String, Object> saveMap = new HashMap<>();
        saveMap.put( "world", location.getWorld().getName() );
        saveMap.put( "x", location.getX() );
        saveMap.put( "y", location.getY() );
        saveMap.put( "z", location.getZ() );
        saveMap.put( "yaw", location.getYaw() );
        saveMap.put( "pitch", location.getPitch() );

        return saveMap;
    }

    @Override
    public Object fromConfig( Class type, Object section, ParameterizedType genericType ) throws Exception {
        Map<String, Object> locationMap;
        if ( section instanceof Map ) {
            locationMap = (Map<String, Object>) section;
        } else {
            locationMap = (Map<String, Object>) ( (ConfigSection) section ).getRawMap();
        }

        Float yaw;
        if ( locationMap.get( "yaw" ) instanceof Double ) {
            Double dYaw = (Double) locationMap.get( "yaw" );
            yaw = dYaw.floatValue();
        } else {
            yaw = (Float) locationMap.get( "yaw" );
        }

        Float pitch;
        if ( locationMap.get( "pitch" ) instanceof Double ) {
            Double dPitch = (Double) locationMap.get( "pitch" );
            pitch = dPitch.floatValue();
        } else {
            pitch = (Float) locationMap.get( "pitch" );
        }

        return new org.bukkit.Location( Bukkit.getWorld( (String) locationMap.get( "world" ) ),
                (Double) locationMap.get( "x" ),
                (Double) locationMap.get( "y" ),
                (Double) locationMap.get( "z" ),
                yaw,
                pitch );
    }

    @Override
    public boolean supports( Class<?> type ) {
        return org.bukkit.Location.class.isAssignableFrom( type );
    }

}
