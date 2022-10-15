package _framework;

public class BtreeNode {
    int val;
    BtreeNode left;
    BtreeNode right;

    public void traverse(BtreeNode root) {

        traverse(root.left);
        //中序遍历  int val = root.val;
        traverse(root.right);
    }
}

