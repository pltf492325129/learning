package practice0827;

public class binaryTreeKMin {
    //拿数组存放已排序的节点值
    int ans,k;
    public int kthSmallest(TreeNode root, int k) {
    //    使用中序排序
        this.k = k;
        dfs(root);
        return ans;
    }

    public void dfs(TreeNode root) {
        if (root == null) return;
        dfs(root.left);
        k--;
        if (k == 0) {
            ans = root.val;
            return;
        }
        dfs(root.right);
    }
}

class TreeNode {
    TreeNode left;
    TreeNode right;
    int val;
    public TreeNode() {}

    public TreeNode(int val) {
        this.val = val;
    }
    public TreeNode(TreeNode left, TreeNode right, int val) {
        this.left = left;
        this.right = right;
        this.val = val;
    }
}
