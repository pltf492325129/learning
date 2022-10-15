package hotPractice;

import java.util.LinkedHashMap;

public class LRUCache {
    public static void main(String[] args) {
    }
    int cap;
    LinkedHashMap<Integer, Integer> cache = new LinkedHashMap<>();

    public LRUCache(int capacity) {
        this.cap = capacity;
    }

    public int get(int key) {
        if (!cache.containsKey(key)) {
            return -1;
        }
        makeRecently(key);
        return  cache.get(key);
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            makeRecently(key);
            return;
        }
        if (cache.size() >= cap) {
            //链表头部最久未使用的。keySet()
            Integer oldestKey = cache.keySet().iterator().next();
            cache.remove(oldestKey);
        }
        cache.put(key, value);
    }
    public void makeRecently(int key) {
        Integer value = cache.get(key);
        cache.remove(key);
        cache.put(key, value);
    }
}
