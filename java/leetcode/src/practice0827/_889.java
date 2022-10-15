package practice0827;

import javax.swing.*;
import java.util.HashMap;

public class _889 {
    HashMap<Integer, Integer> map = new HashMap<>();

    public TreeNode constructFromPrePost(int[] preorder, int[] postorder) {
        for (int i = 0; i < postorder.length; i++) {
            map.put(postorder[i], i);
        }
        TreeNode root = build(preorder, 0, preorder.length - 1,
                postorder, 0, postorder.length - 1);
        return root;


    }

    public TreeNode build(int[] preorder, int preLow, int preHigh,
                          int[] postorder, int postLow, int postHigh) {
        if (preLow > preHigh ) return null;
        if (preLow == preHigh) {
            return new TreeNode(preorder[preLow]);
        }
        int leftRootVal = preorder[preLow + 1];
        int index = map.get(leftRootVal);
        int leftSize = index - postLow + 1;   //*****

        TreeNode root = new TreeNode(preorder[preLow]);
        root.left = build(preorder, preLow + 1, preLow + leftSize,
                postorder, postLow, index);
        root.right = build(preorder, preLow + leftSize + 1, preHigh,
                postorder, index + 1, postHigh);

        return root;
    }


}
