package HOT;

import java.util.*;

public class _3LongestSubString {
    public int lengthOfLongestSubstring(String s) {
        //这里的window是防止重复，记录每个字符出现的次数
        HashMap<Character, Integer> window = new HashMap<>();
        Set<Character> set = new HashSet<>();
        int left = 0;
        int right = 0;
        int max = 0;
        int n = s.length();

        while (right < n) {
            //初始化
            char c = s.charAt(right);
            right++;
            window.put(c, window.getOrDefault(c, 0) + 1);
            //这里是while循环，有重复的会一直去重
            while (window.get(c) > 1) {
                char d = s.charAt(left);
                left++;
                window.put(d, window.get(d)-1);
            }
            /**
             * 注意这里是right - left, 现在windows中放的所有数
             */
            max = Math.max(max, right - left);

        }
        return max;
    }
    public static void main(String[] args) {
        _3LongestSubString longestSubString = new _3LongestSubString();
        int abcabcbb = longestSubString.lengthOfLongestSubstring("pwwkew");
        System.out.println(abcabcbb);


    }
    
}
