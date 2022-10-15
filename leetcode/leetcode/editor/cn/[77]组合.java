//给定两个整数 n 和 k，返回 1 ... n 中所有可能的 k 个数的组合。 
//
// 示例: 
//
// 输入: n = 4, k = 2
//输出:
//[
//  [2,4],
//  [3,4],
//  [2,3],
//  [1,2],
//  [1,3],
//  [1,4],
//] 
// Related Topics 回溯算法 
// 👍 607 👎 0

import java.util.LinkedList;
import java.util.List;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    List<List<Integer>> res = new LinkedList<>();
    public List<List<Integer>> combine(int n, int k) {
        LinkedList<Integer> tracker = new LinkedList<>();
        backtracer(n, k,1, tracker);
        System.out.println(res);
        return res;
    }
    //    路径：track
    //    可选列表
    //    终止条件
    private void backtracer(int n, int k, int begin, LinkedList<Integer> tracker) {
        if (tracker.size() == k) {
            res.add(new LinkedList<>(tracker));
            return ;
        }
        /**
        * 注意这了 i = begin 然后递归传入的数为i+1
        * */
        for (int i = begin; i <= n; i ++){
            //做选择
            tracker.add(i);
            backtracer(n, k, i+1, tracker);
            // 撤销选择
            tracker.removeLast();
        }
    }
}
//leetcode submit region end(Prohibit modification and deletion)
