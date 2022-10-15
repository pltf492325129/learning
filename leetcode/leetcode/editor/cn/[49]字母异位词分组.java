//给定一个字符串数组，将字母异位词组合在一起。字母异位词指字母相同，但排列不同的字符串。
//
// 示例:
//
// 输入: ["eat", "tea", "tan", "ate", "nat", "bat"]
//输出:
//[
//  ["ate","eat","tea"],
//  ["nat","tan"],
//  ["bat"]
//]
//
// 说明：
//
//
// 所有输入均为小写字母。
// 不考虑答案输出的顺序。
//
// Related Topics 哈希表 字符串
// 👍 761 👎 0


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

    //leetcode submit region begin(Prohibit modification and deletion)
    class Solution {
        public List<List<String>> groupAnagrams(String[] strs) {
            //每个单词排序，排完序之后进行比较，把相等的放一块
            if (strs.length == 0) return new ArrayList<>();
            Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

            for (String str : strs) {
                //构造字符排序作为hashtable键
                char[] array = str.toCharArray();
                Arrays.sort(array);
                String key = new String(array);
                //加入散列表
                if (!map.containsKey(key)) {
                    map.put(key, new ArrayList());
                }
                map.get(key).add(str);
            }
            return new ArrayList(map.values());
        }
    }
//leetcode submit region end(Prohibit modification and deletion)
