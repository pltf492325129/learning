package practice0827;

public class _110 {
    private boolean ans = true;
    public boolean isBalanced(TreeNode root) {
        return true;
    }

    public int helper(TreeNode root) {
        if (root == null) return -1;
        int left = helper(root.left);
        int right = helper(root.right);

        if (Math.abs(left - right) > 1) {
            ans = false;
        }
        return Math.max(left, right) + 1;   //*****
    }


}
