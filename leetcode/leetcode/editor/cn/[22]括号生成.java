//数字 n 代表生成括号的对数，请你设计一个函数，用于能够生成所有可能的并且 有效的 括号组合。 
//
// 
//
// 示例 1： 
//
// 
//输入：n = 3
//输出：["((()))","(()())","(())()","()(())","()()()"]
// 
//
// 示例 2： 
//
// 
//输入：n = 1
//输出：["()"]
// 
//
// 
//
// 提示： 
//
// 
// 1 <= n <= 8 
// 
// Related Topics 字符串 回溯算法 
// 👍 1833 👎 0


import java.util.ArrayList;
import java.util.List;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    private List<String> result;
    public List<String> generateParenthesis(int n) {
        result = new ArrayList<String>();
        _helper(0,0, n, "");
        return result;
    }

    private void _helper(int left,int right, int maxLevel, String s) {
        //    1 recursion terminator
        if (left == maxLevel && right == maxLevel) {
            System.out.println(s);
            result.add(s);
            return ;
        }
        //    2 process logic in current level: left right
        //    3 drill down
        if (left < maxLevel) _helper(left + 1,right, maxLevel, s + "(");
        if (right < left) _helper(left,right + 1, maxLevel, s + ")" );
        //    4 restor curren status
    }
}
//leetcode submit region end(Prohibit modification and deletion)
