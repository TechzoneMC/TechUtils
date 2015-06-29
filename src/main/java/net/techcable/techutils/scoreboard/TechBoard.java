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
package net.techcable.techutils.scoreboard;

import lombok.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.techcable.techutils.scheduler.TechScheduler;

import com.google.common.collect.ImmutableList;

public abstract class TechBoard {

    protected TechBoard() {
        TechScheduler.scheduleAsyncTask(new Runnable() {

            @Override
            public void run() {
                aquireLock();
                try {
                    flush();
                } finally {
                    releaseLock();
                }
            }
        }, 0, 1);
    }

    private final Lock lock = new ReentrantLock();

    public void display() {
        aquireLock();
        try {
            display0();
        } finally {
            releaseLock();
        }
    }

    public void hide() {
        aquireLock();
        try {
            hide0();
        } finally {
            releaseLock();
        }
    }

    protected abstract void display0();

    protected abstract void hide0();

    protected abstract void setScore(String name, int score);

    protected abstract void flush();

    protected abstract void removeScore(final String name);

    private final Map<String, ScoreboardElement> elements = new HashMap<>();
    private final List<ScoreboardElement> elementList = new LinkedList<>();

    public ImmutableList<ScoreboardElement> getElements() {
        return ImmutableList.copyOf(elementList);
    }

    public void aquireLock() {
        lock.lock();
    }

    public void releaseLock() {
        lock.unlock();
    }

    public void addElement(String name) {
        addElement(name, elementList.size() + 1);
    }

    public void addElement(String name, int value) {
        aquireLock();
        try {
            ScoreboardElement element = new ScoreboardElement(name, value);
            elements.put(name, element);
            int listIndex = elementList.size() + 1;
            element.listIndex = listIndex;
            elementList.add(listIndex, element);
            setScore(name, value);
        } finally {
            releaseLock();
        }
    }

    public void setElement(String name, int value) {
        aquireLock();
        try {
            ScoreboardElement element = elements.get(name);
            if (element == null) {
                addElement(name, value);
                return; // Don't worry, java flow control executes finally before a return
            }
            int listIndex = element.listIndex;
            element = new ScoreboardElement(name, value);
            elements.put(name, element);
            elementList.set(listIndex, element);
        } finally {
            releaseLock();
        }
    }

    public void clear() {
        aquireLock();
        try {
            for (ScoreboardElement element : elementList) {
                removeScore(element.name);
            }
            elementList.clear();
            elements.clear();
        } finally {
            releaseLock();
        }
    }

    public void cleanup() {
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = {"name", "score"})
    @Getter
    public static class ScoreboardElement {

        private final String name;
        private final int score;
        @Getter(AccessLevel.NONE)
        private int listIndex;
    }
}
