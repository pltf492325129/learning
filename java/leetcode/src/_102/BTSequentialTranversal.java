package _102;

import java.util.*;

public class BTSequentialTranversal {
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


class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
