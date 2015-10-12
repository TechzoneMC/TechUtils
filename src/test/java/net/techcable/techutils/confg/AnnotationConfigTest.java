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
package net.techcable.techutils.confg;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import net.techcable.techutils.config.AnnotationConfig;

import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class AnnotationConfigTest<C extends TestConfig> {

    @Test
    public void testDefaultLoad() throws IOException, InvalidConfigurationException {
        C config = newTestConfig();
        File tempFile = File.createTempFile("config", ".txt");
        if (tempFile.exists()) {
            assertTrue(tempFile.delete());
        }
        load(config, tempFile, AnnotationConfigTest.class.getResource(getDefaultResource()));
        assertEquals(12, config.getExample1());
        assertArrayEquals(new String[] {"Test", "Mo Test"}, config.getExample2().toArray(new String[config.getExample2().size()]));
        assertEquals("Yr Mum", config.getExample4());
        assertEquals("Is very nice", config.getExample5());
        assertEquals("I'm happy today", config.getExample6());
        assertEquals(Food.POTATO, config.getFood());
        assertEquals(15, config.getTime());
    }

    @Test
    public void testOverridenLoad() throws IOException, InvalidConfigurationException {
        C config = newTestConfig();
        File tempFile = File.createTempFile("config", ".txt");
        URL modifiedURL = Resources.getResource(getClass(), getModifiedResource());
        CharSource in = Resources.asByteSource(modifiedURL).asCharSource(Charsets.UTF_8);
        CharSink out = Files.asCharSink(tempFile, Charsets.UTF_8);
        in.copyTo(out);
        load(config, tempFile, AnnotationConfig.class.getResource(getDefaultResource()));
        assertEquals(112, config.getExample1());
        assertArrayEquals(new String[] {"This is", "stupid test"}, config.getExample2().toArray(new String[config.getExample2().size()]));
        assertEquals("Yr Mum", config.getExample4());
        assertEquals("Is ugly", config.getExample5()); // Overridden but still default
        assertEquals("I'm happy today", config.getExample6()); // Still has default config value, not specified in default config
        assertEquals(Food.TACO, config.getFood());
        assertEquals(TimeUnit.HOURS.toSeconds(30), config.getTime());
    }

    @Test
    public void testLoadSaveEquals() throws IOException, InvalidConfigurationException {
        C config = newTestConfig();
        File tempFile = File.createTempFile("config", ".yml");
        URL modifiedURL = Resources.getResource(getClass(), getModifiedResource());
        URL defaultURL = Resources.getResource(getClass(), getDefaultResource());
        CharSource originalIn = Resources.asByteSource(modifiedURL).asCharSource(Charsets.UTF_8);
        CharSink out = Files.asCharSink(tempFile, Charsets.UTF_8);
        CharSource fileIn = Files.asCharSource(tempFile, Charsets.UTF_8);
        originalIn.copyTo(out);
        load(config, tempFile, defaultURL);
        save(config, tempFile, defaultURL);
        List<String> originalLines = originalIn.readLines();
        originalLines = Lists.transform(originalLines, (s) -> s.replaceAll("\\s", "")); // Strip whitespace
        List<String> fileLines = fileIn.readLines();
        fileLines = Lists.transform(fileLines, (s) -> s.replaceAll("\\s", "")); // Strip whitespace
        assertEquals(originalLines, fileLines);
    }

    public abstract String getModifiedResource();

    public abstract String getDefaultResource();

    protected abstract C newTestConfig();

    protected abstract void load(C config, File file, URL defaultURL) throws IOException, InvalidConfigurationException;

    protected abstract void save(C config, File file, URL defaultURL) throws IOException, InvalidConfigurationException;

}
