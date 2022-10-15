//给定一个二叉树，返回其节点值的锯齿形层序遍历。（即先从左往右，再从右往左进行下一层遍历，以此类推，层与层之间交替进行）。 
//
// 例如： 
//给定二叉树 [3,9,20,null,null,15,7], 
//
// 
//    3
//   / \
//  9  20
//    /  \
//   15   7
// 
//
// 返回锯齿形层序遍历如下： 
//
// 
//[
//  [3],
//  [20,9],
//  [15,7]
//]
// 
// Related Topics 树 广度优先搜索 二叉树 
// 👍 525 👎 0


//leetcode submit region begin(Prohibit modification and deletion)

import java.util.LinkedList;
import java.util.List;

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> res = new LinkedList<>();
        if (root == null) return res;
        Queue<TreeNode> que = new LinkedList();
        que.offer(root);
        int odd = 0;
        while (!que.isEmpty()) {
            odd++;
            int size = que.size();
            LinkedList<Integer> list = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                TreeNode cur = que.poll();
                if (odd % 2 == 1) {
                    list.add(cur.val);
                }else {
                    list.addFirst(cur.val);
                }
                if (cur.left != null) {
                    que.offer(cur.left);
                }
                if (cur.right != null) {
                    que.offer(cur.right);
                }
            }
            res.add(list);
        }
        return res;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
