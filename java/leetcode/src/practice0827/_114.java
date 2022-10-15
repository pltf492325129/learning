package practice0827;

public class _114 {
    public void flatten(TreeNode root) {
        if (root == null) return;
        flatten(root.left);
        flatten(root.right);
        TreeNode left = root.left;
        TreeNode right = root.right;

        root.left = null;
        root.right = left;

        TreeNode p = root;
        while (p.right != null) {
            p = p.right;
        }
        p.right = right;
    }

    public TreeNode dfs(TreeNode root) {
        if (root == null) return null;

        //遍历左右子树，全部拉成链表
        TreeNode left = dfs(root.left);
        TreeNode right = dfs(root.right);
        //将左子树的所有结点都连接到右子树上

        root.left = null;
        root.right = left;

        TreeNode p = root;
        while (p.right != null) {
            p = p.right;
        }

        p.right = right;
        return root;
    }

    TreeNode dummy = new TreeNode(-1);
    TreeNode p = dummy;
    void traverse(TreeNode root) {
        if (root == null) return;
        p.right = new TreeNode(root.val);
        p = p.right;
        traverse(root.left);
        traverse(root.right);
    }

}
