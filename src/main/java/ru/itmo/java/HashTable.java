package ru.itmo.java;

import java.util.Map;

public class HashTable {
    private final double loadFactor;
    private int threshold, objectsInData;
    private Entry[] data;

    private static final double DEFAULT_LOAD_FACTOR = 0.5;
    private static final int DEFAULT_SIZE = 101;

    public HashTable() {
        this(DEFAULT_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(int size) {
        this(size, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(double loadFactor) {
        this(DEFAULT_SIZE, loadFactor);
    }

    public HashTable(int size, double loadFactor) {
        data = new Entry[size];
        for (int i = 0; i < data.length; i++) {
            data[i] = new Entry(null, null);
        }
        this.loadFactor = loadFactor;
        this.threshold = (int)Math.ceil(this.loadFactor * this.data.length);
        this.objectsInData = 0;
    }

    public Object put(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException();
        }

        Entry newObject = new Entry(key, value);
        Object toReturn = null;
        Integer index = keyToRealIndex(key);

        if (index != null) {
            toReturn = data[index].value;
            data[index].value = value;
        } else {
            if (this.objectsInData == threshold) {
                resize();
            }

            index = newObject.hashCode % data.length;

            while (data[index].key != null) {
                index++;
                if (index == data.length) {
                    index = 0;
                }
            }

            data[index] = newObject;
            objectsInData++;
        }

        return toReturn;
    }

    public Object get(Object key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        Integer indexInArray = keyToRealIndex(key);

        if (indexInArray == null) {
            return null;
        }

        return data[indexInArray].value;
    }

    public Object remove(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }

        Integer indexInArray = keyToRealIndex(key);
        Object result = null;

        if (indexInArray != null) {
            result = data[indexInArray].value;
            data[indexInArray].deleted = true;
            data[indexInArray].key = data[indexInArray].value = null;
            data[indexInArray].hashCode = -1;
            objectsInData--;
        }

        return result;
    }

    public int size() {
        return this.objectsInData;
    }

    private void resize() {
        Entry[] oldData = this.data;
        this.data = new Entry[oldData.length * 2];
        objectsInData = 0;
        threshold = (int)Math.ceil(this.data.length * loadFactor);
        for (int i = 0; i < data.length; i++) {
            data[i] = new Entry(null, null);
        }

        for (int i = 0; i < oldData.length; i++) {
            if (oldData[i] == null) {
                continue;
            }
            if (oldData[i].hashCode != -1) {
                put(oldData[i].key, oldData[i].value);
            }
        }
    }

    private Integer keyToRealIndex(Object key) {
        int hash = Math.abs(key.hashCode());
        int searchFirstIndex = hash % data.length;
        Integer index = null;

        for (int i = searchFirstIndex; i < data.length; i++) {
            if (data[i] == null) {
                return index;
            }
            if (data[i].hashCode == hash && data[i].key.equals(key)) {
                index = i;
                break;
            }
            if (i == data.length - 1) {
                i = -1;
            }
            if (i == searchFirstIndex - 1 ||
                    (i != -1 && data[i].value == null && !data[i].deleted)) {
                index = null;
                break;
            }
        }

        return index;
    }

    private static class Entry {
        private Object key, value;
        private int hashCode;
        private boolean deleted;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
            this.hashCode = this.key != null ? Math.abs(this.key.hashCode()) : -1;
            this.deleted = false;
        }
    }
}
