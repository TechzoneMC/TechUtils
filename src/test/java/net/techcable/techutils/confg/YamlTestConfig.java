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

import lombok.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.techcable.techutils.config.Setting;
import net.techcable.techutils.config.Time;
import net.techcable.techutils.config.YamlAnnotationConfig;

@Getter
public class YamlTestConfig extends YamlAnnotationConfig implements TestConfig {
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

    @Setting("time")
    @Time(value = TimeUnit.SECONDS, as = TimeUnit.SECONDS)
    private int time;

    @Setting("food")
    private Food food;
}
