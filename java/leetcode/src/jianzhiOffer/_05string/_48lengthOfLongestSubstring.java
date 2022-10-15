package jianzhiOffer._05string;

import java.util.HashMap;

public class _48lengthOfLongestSubstring {
    public int lengthOfLongestSubstring(String s) {
        HashMap<Character, Integer> window = new HashMap<>();
        int left = 0, right = 0;
        int res = 0;
        while (right < s.length()) {
            char c = s.charAt(right);
            window.put(c, window.getOrDefault(c, 0) + 1);
            right++;
            //这里的含义是 排除重复值
            while (window.get(c) > 1) {
                char d = s.charAt(left);
                window.put(d, window.get(d) - 1);
                left++;
            }
            //注意这里更新res
            res = Math.max(res, right - left);
        }
        return res;
    }
}
