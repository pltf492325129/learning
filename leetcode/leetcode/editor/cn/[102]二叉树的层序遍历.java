 //给你一个二叉树，请你返回其按 层序遍历 得到的节点值。 （即逐层地，从左到右访问所有节点）。
//
// 
//
// 示例： 
//二叉树：[3,9,20,null,null,15,7], 
//
// 
//    3
//   / \
//  9  20
//    /  \
//   15   7
// 
//
// 返回其层序遍历结果： 
//
// 
//[
//  [3],
//  [9,20],
//  [15,7]
//]
// 
// Related Topics 树 广度优先搜索 二叉树 
// 👍 907 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
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
    public List<List<Integer>> levelOrder(TreeNode root) {
        return BFS(root);
    }

    private List<List<Integer>> BFS(TreeNode root) {
        Queue<TreeNode> q = new LinkedList<>();
        List<List<Integer>> res = new LinkedList<>();
        /**
         * 每次记得考虑边界条件 root == null
         */
        if (root == null)  return res;

        q.offer(root);
        while (!q.isEmpty()) {
            /**
             * 每层遍历完进来重新初始化；
             */
            List<Integer> levelRes = new ArrayList<>();

            int size = q.size();
            for (int i = 0; i < size; i++) {
                TreeNode cur = q.poll();
                levelRes.add(cur.val);
                if (cur.left != null) {
                    q.offer(cur.left);
                }
                if (cur.right != null) {
                    q.offer(cur.right);
                }
            }
            res.add(levelRes);
        }
        return res;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
