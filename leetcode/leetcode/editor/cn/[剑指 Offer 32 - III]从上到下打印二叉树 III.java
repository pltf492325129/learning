//请实现一个函数按照之字形顺序打印二叉树，即第一行按照从左到右的顺序打印，第二层按照从右到左的顺序打印，第三行再按照从左到右的顺序打印，其他行以此类推。 
//
// 
//
// 例如: 
//给定二叉树: [3,9,20,null,null,15,7], 
//
//     3
//   / \
//  9  20
//    /  \
//   15   7
// 
//
// 返回其层次遍历结果： 
//
// [
//  [3],
//  [20,9],
//  [15,7]
//]
// 
//
// 
//
// 提示： 
//
// 
// 节点总数 <= 1000 
// 
// Related Topics 树 广度优先搜索 二叉树 
// 👍 119 👎 0


//leetcode submit region begin(Prohibit modification and deletion)

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
class Solution {
     public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        Queue<TreeNode> que = new LinkedList<>();
        if (root != null) que.add(root);

        while (!que.isEmpty()) {
            LinkedList<Integer> levelRes = new LinkedList<>();

            for (int i = que.size(); i > 0; i--) {
                TreeNode cur = que.poll();
                if (res.size() % 2 == 0) levelRes.addLast(cur.val);
                else levelRes.addFirst(cur.val);
                if (cur.left != null)  que.add(cur.left);
                if (cur.right != null) que.add(cur.right);
            }

            res.add(levelRes);
        }

        return res;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
