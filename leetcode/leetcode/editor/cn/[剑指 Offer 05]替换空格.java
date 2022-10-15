//请实现一个函数，把字符串 s 中的每个空格替换成"%20"。 
//
// 
//
// 示例 1： 
//
// 输入：s = "We are happy."
//输出："We%20are%20happy." 
//
// 
//
// 限制： 
//
// 0 <= s 的长度 <= 10000 
// Related Topics 字符串 
// 👍 145 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public String replaceSpace(String s) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            //由于每次是一个字符替换成三个字符，所以这样还不可以，数组操作会更方便些。
            if (s.charAt(i) != ' '){
                /**
                 * res 使用的是 append方法
                 */
                res.append(s.charAt(i));
            }
            else if(s.charAt(i) == ' ') {
                res.append("%20");
            }
        }
        return res.toString();
    }
}
//leetcode submit region end(Prohibit modification and deletion)
