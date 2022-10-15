package hotPractice;

import java.util.HashMap;

public class TreeTest01 {
    HashMap<Integer, Integer> indexMap;
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        //terminator
        int n = preorder.length;
       indexMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < n; i++) {
            indexMap.put(inorder[i], i);
        }

        return myBuildTree(preorder, inorder, 0, n - 1, 0, n - 1);
    }

    private TreeNode myBuildTree(int[] preorder, int[] inorder, int preorder_left, int preorder_right, int inorder_left, int inorder_right) {

        //找到左子树中的根节点
        int preorder_root = preorder_left;
        //中序遍历中的根节点
        int inorder_root = indexMap.get(inorder[preorder_root]);
        //根节点
        TreeNode root = new TreeNode(preorder[preorder_left]);

        //得到左子树中的节点数目
        int size_left_subtree = inorder_root - inorder_left;

        root.left = myBuildTree(preorder, inorder,preorder_left+1, preorder_left + size_left_subtree, inorder_left, inorder_root-1);
        root.right = myBuildTree(preorder, inorder, preorder_left+1+size_left_subtree, preorder_right, inorder_root+1 , inorder_right);
        return root;
    }
}

class TreeNode {
     int val;
     TreeNode left;
     TreeNode right;
     TreeNode() {}
     TreeNode(int val) { this.val = val; }
     TreeNode(int val, TreeNode left, TreeNode right) {
         this.val = val;
         this.left = left;
         this.right = right;
     }
 }