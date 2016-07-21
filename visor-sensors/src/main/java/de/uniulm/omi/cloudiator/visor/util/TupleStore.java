/*
 * Copyright (c) 2014-2016 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.visor.util;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 11.07.16.
 */
public class TupleStore<E, F> implements Map<E, F> {

    private Map<E, F> oldObjects;
    private Map<E, F> currentObjects;


    public static class Tuple<F> {

        private final F current;
        private final Optional<F> old;

        public static <G> Tuple<G> of(G current, @Nullable G old) {
            return new Tuple<>(current, old);
        }

        private Tuple(F current, @Nullable F old) {
            checkNotNull(current, "current null");
            this.current = current;
            this.old = Optional.ofNullable(old);
        }

        public F current() {
            return current;
        }

        public Optional<F> old() {
            return old;
        }
    }

    private TupleStore(int expectedSize) {
        //todo validate expected size?
        this.oldObjects = new HashMap<>(expectedSize);
        this.currentObjects = new HashMap<>(expectedSize);
    }

    public static <K, V> TupleStore<K, V> createWithExpectedSize(int expectedSize) {
        return new TupleStore<>(expectedSize);
    }

    public static <K, V> TupleStore<K, V> create() {
        return new TupleStore<>(10);
    }

    @Override public int size() {
        return currentObjects.size();
    }

    @Override public boolean isEmpty() {
        return currentObjects.isEmpty();
    }

    @Override public boolean containsKey(Object key) {
        return currentObjects.containsKey(key);
    }

    @Override public boolean containsValue(Object value) {
        return currentObjects.containsValue(value);
    }

    @Override public F get(Object key) {
        return currentObjects.get(key);
    }

    @Override public F put(E key, F value) {
        F oldValue = null;
        if (currentObjects.containsKey(key)) {
            oldValue = currentObjects.get(key);
            oldObjects.put(key, oldValue);
        }
        currentObjects.put(key, value);
        return oldValue;
    }

    @Override public F remove(Object key) {
        return null;
    }

    @Override public void putAll(Map<? extends E, ? extends F> m) {
        m.forEach(this::put);
    }

    @Override public void clear() {
        this.currentObjects.clear();
        this.oldObjects.clear();
    }

    @Override public Set<E> keySet() {
        return currentObjects.keySet();
    }

    @Override public Collection<F> values() {
        return currentObjects.values();
    }

    @Override public Set<Entry<E, F>> entrySet() {
        return currentObjects.entrySet();
    }

    public Tuple<F> getTuple(E key) {
        if (!currentObjects.containsKey(key)) {
            throw new NoSuchElementException();
        }
        return Tuple.of(currentObjects.get(key), oldObjects.get(key));
    }



}
