package jianzhiOffer.day3;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class _50firstUniqChar {
    public char firstUniqChar(String s) {
        /**
         * 有序哈希链表
         */
        Map<Character, Boolean> map = new LinkedHashMap<>();
        char[] sc = s.toCharArray();
        for (char c : sc) {
            map.put(c, !map.containsKey(c));
        }
        for (Map.Entry<Character, Boolean> d : map.entrySet()) {
            if (d.getValue()){
                return d.getKey();
            }
        }
        return ' ';
    }
    public char firstUniqChar2(String s) {
        Map<Character, Integer> map = new HashMap<>();
        char[] sc = s.toCharArray();
        for (Character c : sc) {
            if (map.containsKey(c)) {
                map.put(c, map.get(c) + 1);
            } else {
                map.put(c, 1);
            }
        }
        /**
         * 这里又重新遍历了一次， 为的是选出第一个没有被重复的值
         */
        for (Character c : sc) {
            if (map.get(c) == 1) {
                return c;
            }
        }
        return ' ';

    }
}
