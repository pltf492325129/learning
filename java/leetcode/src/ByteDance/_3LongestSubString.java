package ByteDance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class _3LongestSubString {
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> window = new HashMap<>();

        int left = 0;
        int right = 0;
        int size = 0;
        while (right < s.length()) {
            char d = s.charAt(right);
            window.put(d,
                    window.getOrDefault(d, 0) + 1);
            right++;
            while (window.get(d) > 1) {
                char c = s.charAt(left);
                window.put(c, window.get(c) - 1);
                left++;
            }
            size = Math.max(size, right - left + 1);
        }
        return size;
    }
}
