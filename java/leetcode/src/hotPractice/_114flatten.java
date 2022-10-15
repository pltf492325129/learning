package hotPractice;

import java.util.LinkedList;
import java.util.List;

public class _114flatten {

    public void flatten(TreeNode root) {
        List<TreeNode> list = new LinkedList<>();
        preorderTraversal(root, list);
        int size = list.size();
        for (int i = 1; i < size; i++) {
            TreeNode prev = list.get(i - 1);
            TreeNode curr = list.get(i);
            prev.left = null;
            prev.right = curr;
        }
    }

    public static void preorderTraversal(TreeNode root, List<TreeNode> list) {
        if (root != null) {
            list.add(root);
            preorderTraversal(root.left, list);
            preorderTraversal(root.right, list);
        }
    }
}
 //class TreeNode {
 //    int val;
 //    TreeNode left;
 //    TreeNode right;
 //    TreeNode() {}
 //    TreeNode(int val) { this.val = val; }
 //    TreeNode(int val, TreeNode left, TreeNode right) {
 //        this.val = val;
 //        this.left = left;
 //        this.right = right;
 //    }
 //}