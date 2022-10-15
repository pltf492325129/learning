package ByteDance;


import java.util.LinkedHashMap;
import java.util.Map;

public class _146 {
    public static void main(String[] args) {

    }

}
class LRUCache {
    LinkedHashMap<Integer, Integer> cache = new LinkedHashMap<>();
    int size;
    public LRUCache(int capacity) {
        this.size = capacity;
    }

    public int get(int key) {
        if (!cache.containsKey(key)) {
            return -1;
        }
        makeRecentlyUsed(key);
        return cache.get(key);
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            cache.put(key, value);
            makeRecentlyUsed(key);
            return;
        }
        if (cache.size() >= size) {
            cache.remove(cache.entrySet().iterator().next());
        }
        cache.put(key, value);
    }
    public void makeRecentlyUsed(int key) {
        int val = cache.get(key);
        cache.remove(key);
        cache.put(key, val);
    }
}
