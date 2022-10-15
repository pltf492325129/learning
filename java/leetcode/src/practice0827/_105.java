package practice0827;

import java.util.HashMap;

public class _105 {
    HashMap<Integer, Integer> map = new HashMap<>();

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        for (int i = 0; i < inorder.length; i++) {
            map.put(inorder[i], i);
        }
        TreeNode root = build(preorder, 0, preorder.length - 1,
                inorder, 0, inorder.length - 1);
        return root;
    }

    public TreeNode build(int[] preorder, int preLeft, int preRight,
                          int[] inorder, int inLeft, int inRight) {
        if (preLeft > preRight || inLeft > inRight) return null;
        //构建根节点
        int leftsize = 0;
        int indexRoot = map.get(preorder[preLeft]);
        leftsize = indexRoot - inLeft;

        TreeNode root = new TreeNode(preorder[preLeft]);
        root.left = build(preorder, preLeft + 1, preLeft + leftsize,
                inorder, inLeft, indexRoot - 1);

        root.right = build(preorder, preLeft + leftsize + 1, preRight,
                inorder, indexRoot + 1, inRight);
        return root;
    }
}
