package _framework;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class BtreeLevel {
    public static void main(String[] args) {


    }

    public void transverse(TreeNode root) {
        LinkedList<LinkedList<Integer>> res = new LinkedList<>();

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int sz = queue.size();
            LinkedList<Integer> temp = null;
            for (int i = 0; i < sz; i++) {
                temp = new LinkedList<>();
                TreeNode cur = queue.poll();
                temp.add(cur.val);
                if (cur.left != null) {
                    queue.offer(cur.left);
                }
                if (cur.right != null) {
                    queue.offer(cur.right);
                }
            }
            res.add(temp);
        }
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    public TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
