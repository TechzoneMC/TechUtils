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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import net.techcable.techutils.config.AnnotationConfig;
import net.techcable.techutils.config.Setting;

import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.Test;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

import static org.junit.Assert.*;

public class AnnotationConfigTest {

    @Test
    public void testDefaultLoad() throws IOException, InvalidConfigurationException {
        TestConfig config = new TestConfig();
        File tempFile = File.createTempFile("config", ".yml");
        if (tempFile.exists()) {
            tempFile.delete();
        }
        config.load(tempFile, AnnotationConfigTest.class.getResource("/test/default.yml"));
        assertEquals(12, config.example1);
        assertArrayEquals(new String[]{"Test", "Mo Test"}, config.example2.toArray(new String[config.example2.size()]));
        assertEquals("Yr Mum", config.example4);
        assertEquals("Is very nice", config.example5);
        assertEquals("I'm happy today", config.example6);
    }

    @Test
    public void testOverridenLoad() throws IOException, InvalidConfigurationException {
        TestConfig config = new TestConfig();
        File tempFile = File.createTempFile("config", ".yml");
        InputStream in = AnnotationConfigTest.class.getResourceAsStream("/test/modified.yml");
        OutputStream out = new FileOutputStream(tempFile);
        try {
            ByteStreams.copy(in, out);
        } finally {
            Closeables.close(in, true);
            Closeables.close(out, true);
        }
        config.load(tempFile, AnnotationConfig.class.getResource("/test/default.yml"));
        assertEquals(112, config.example1);
        assertArrayEquals(new String[]{"This is", "stupid test"}, config.example2.toArray(new String[config.example2.size()]));
        assertEquals("Yr Mum", config.example4);
        assertEquals("Is ugly", config.example5); // Overridden but still default
        assertEquals("I'm happy today", config.example6); // Still has default config value, not specified in default config
    }

    public static class TestConfig extends AnnotationConfig {

        @Setting("example1")
        private int example1;

        @Setting("example2")
        private List<String> example2;

        @Setting("example3.example4")
        private String example4;

        @Setting("example3.example5")
        private String example5;

        @Setting("example6")
        private String example6;
    }
}
