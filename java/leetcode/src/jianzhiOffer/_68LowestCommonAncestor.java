package jianzhiOffer;

public class _68LowestCommonAncestor {
    public TreeNode bowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        //1 q或者p都存在以root为根的树中, 则root为最近公共祖先
        //2 p或q都不存在以root为根的书中，则返回null
        //3 p或者q只有一个存在以root为根节点的树中，则返回那个**节点**

        //base case
        // 1 root == null return null
        // 2 root = p 如果q存在p为节点的书中，则返回p; 若不存在p中，按第三种情况讨论
        if (root == null) return null;
        if (root == p || root == q) return root;
        TreeNode left = bowestCommonAncestor(root.left, p, q);
        TreeNode right = bowestCommonAncestor(root.right, p, q);

        if (left != null && right != null)return root;
        if (left == null && right == null) return null;
        return left == null ? right : left;

    }

}
