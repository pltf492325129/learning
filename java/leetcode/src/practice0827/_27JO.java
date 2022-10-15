package practice0827;

public class _27JO {

    //遍历方法求解
    public void traverse(TreeNode root) {
        if (root == null) return;
        TreeNode tmp = root.right;
        root.right = root.left;
        root.left = tmp;

        traverse(root.left);
        traverse(root.right);

    }

    TreeNode invertTree(TreeNode root) {
        if (root == null) return null;
        TreeNode leftNode = invertTree(root.left);
        TreeNode rightNode = invertTree(root.right);
        root.right = leftNode;
        root.left = rightNode;
        return root;
    }
}
