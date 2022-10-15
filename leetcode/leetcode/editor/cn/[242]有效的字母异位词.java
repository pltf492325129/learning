//给定两个字符串 s 和 t ，编写一个函数来判断 t 是否是 s 的字母异位词。 
//
// 示例 1: 
//
// 输入: s = "anagram", t = "nagaram"
//输出: true
// 
//
// 示例 2: 
//
// 输入: s = "rat", t = "car"
//输出: false 
//
// 说明: 
//你可以假设字符串只包含小写字母。 
//
// 进阶: 
//如果输入字符串包含 unicode 字符怎么办？你能否调整你的解法来应对这种情况？ 
// Related Topics 排序 哈希表 
// 👍 394 👎 0


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean isAnagram(String s, String t) {
    //  1、排序长度是否一致，若一致就比较各个位置的元素
    //    2、使用hashtable来存放s中各个元素， 拿t中各个元素和hash表中的元素比较，
        //    若在表中，则该位置上的元素个数-1，如果为-1，则直接返回，直到比较完t中所有元素。
        if (s.length() != t.length()) {
            return false;
        }
        int[] table = new int[26];

        for (int i = 0; i < s.length(); i++) {
            table[s.charAt(i) - 'a']++;
        }
        for (int j = 0; j < t.length(); j++) {
            table[t.charAt(j) - 'a']--;
            if (table[t.charAt(j) - 'a'] < 0) {
                return false;
            }
        }
        return true;

/*        if (s.length() != t.length()) {
            return false;
        }

        Map<Character, Integer> map = new HashMap<Character, Integer>();
        for (char c : s.toCharArray()) {
            Integer count = map.get(c);
            count = count == null ? 1 : ++count;
            map.put(c, count);
        }
        for (Character c : t.toCharArray()) {
            if (map.containsKey(c)) {
                Integer count = map.get(c);
                count = count - 1;
                if (count == -1) {
                    return false;
                }
                map.put(c, count);
            } else {
                return false;
            }
        }
        return true;*/


/*        char[] sarray = s.toCharArray();
        Arrays.sort(sarray);
        char[] tarray = t.toCharArray();
        Arrays.sort(tarray);
        return Arrays.equals(sarray, tarray);*/
    }
}
//leetcode submit region end(Prohibit modification and deletion)
