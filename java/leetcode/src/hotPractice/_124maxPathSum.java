package hotPractice;

public class _124maxPathSum {
    static int maxGain = Integer.MIN_VALUE;
    public int maxPathSum(TreeNode root) {
        dp(root);
        return maxGain;
    }

    public int dp(TreeNode root) {
        if (root == null) return 0;

        int leftGain = Math.max(dp(root.left), 0);
        int rightGain = Math.max(dp(root.right), 0);

        int lmr = root.val + leftGain + rightGain;//左右之和
        int lor = root.val + Math.max(leftGain, rightGain);//左或者右之和
        maxGain = Math.max(lmr, maxGain);
        //注意这里返回的是其中一边的最大值
        return lor;
    }

    public static void main(String[] args) {
        TreeNode treeNode = new TreeNode(1);
        treeNode.left = new TreeNode(2);
        treeNode.right = new TreeNode(3);
        int i = new _124maxPathSum().maxPathSum(treeNode);
        System.out.println(i);
    }
}
