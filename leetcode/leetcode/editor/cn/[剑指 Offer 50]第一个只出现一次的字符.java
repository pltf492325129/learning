//在字符串 s 中找出第一个只出现一次的字符。如果没有，返回一个单空格。 s 只包含小写字母。 
//
// 示例: 
//
// s = "abaccdeff"
//返回 "b"
//
//s = "" 
//返回 " "
// 
//
// 
//
// 限制： 
//
// 0 <= s 的长度 <= 50000 
// Related Topics 队列 哈希表 字符串 计数 
// 👍 121 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
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

}
//leetcode submit region end(Prohibit modification and deletion)
