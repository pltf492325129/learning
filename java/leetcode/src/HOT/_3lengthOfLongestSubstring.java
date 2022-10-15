package HOT;

import java.util.HashSet;
import java.util.Set;

public class _3lengthOfLongestSubstring {
    public int lengthOfLongestSubstring(String s) {
        Set<Character> window = new HashSet<>();
        char[] chars = s.toCharArray();

        //滑动窗口法
        int left = 0, right = 0;
        int max = 0;
        while (right < s.length()) {

            //扩大窗口
            if (!window.contains(chars[right]) && right < s.length()) {
                window.add(chars[right]);
                max = Math.max(max, window.size());
                right++;
            }
            //缩小窗口
            if (window.contains(chars[right]) && right < s.length()) {
                left++;
                window.remove(chars[left]);
            }
        }
        return max;
    }
}
