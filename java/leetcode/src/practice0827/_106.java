package practice0827;

import java.util.HashMap;

public class _106 {
    HashMap<Integer, Integer> map = new HashMap<>();

    TreeNode buildTree(int[] inorder, int[] postorder) {
        for (int i = 0; i < inorder.length; i++) {
            map.put(inorder[i], i);
        }
        TreeNode root = build(inorder, 0, inorder.length - 1,
                postorder, 0, postorder.length - 1);
        return root;
    }


    TreeNode build(int[] inorder, int inLeft, int inRight,
                   int[] postorder, int postLeft, int postRight) {
        if (inLeft > inRight || postLeft > postRight) return null;
        int rootIndex = map.get(postorder[postRight]);
        int leftSize = rootIndex - inLeft;

        TreeNode root = new TreeNode(postorder[postRight]);
        root.left = build(inorder, inLeft, rootIndex - 1,
                postorder, postLeft, postLeft + leftSize - 1);
        root.right = build(inorder, rootIndex + 1, inRight,
                postorder, postLeft + leftSize, postRight - 1);
        return root;
    }
}
