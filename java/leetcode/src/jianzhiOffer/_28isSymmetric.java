package jianzhiOffer;

public class _28isSymmetric {
    public boolean isSymmetric(TreeNode root) {
        return root == null ? true : recur(root.left, root.right);
    }

    public boolean recur(TreeNode L, TreeNode R) {
        if (L == null && R == null) {
            return true;
        }
        if (L == null || R == null || L.val != R.val) {
            return false;
        }
        return recur(L.left, R.right)&& recur(L.right, R.left);
    }
/*    public boolean isSymmetric(TreeNode root) {
        if (root.left==null && root.right==null) return true;
        if (root.left.val == root.right.val) {
            isSymmetric(root.left);
            isSymmetric(root.right);
        } else if (root.left == null || root.right == null || root.left.val != root.right.val) {
            return false;
        }
        return false;
    }*/

    public static void main(String[] args) {

    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    public TreeNode(int x) {
        val = x;
    }
}

