package jianzhiOffer;

public class _54KthLargest {
    int res,k;
    public int kthLargest(TreeNode root, int k) {
        this.k = k;
        inTraverse(root);
        return res;

    }
    public void inTraverse(TreeNode root) {
        if (root == null || k <= 0) return;

        inTraverse(root.right);
        //1注意这里，设置了全局变量k， 并且是 --k 先操作后赋值 2 遍历是先右子树后左子树，就为倒序
        if (--k == 0) {
            res = root.val;
        }
        inTraverse(root.left);
    }

    public static void main(String[] args) {
        int m = 4;
        System.out.println(m);
        if (--m >= 0) {
            System.out.println(m);
        }
    }
}
