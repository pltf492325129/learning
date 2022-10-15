package jianzhiOffer;

import java.util.HashMap;

public class _07BuildTree {
    HashMap<Integer, Integer> dic = new HashMap<>();
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        int n = inorder.length;
        for (int i = 0; i < n; i++) {
            dic.put(inorder[i], i);
        }
        //myBuildTree(preorder,inorder)
        return myBuildTree(preorder, inorder, 0, n - 1, 0, n - 1);
    }

    public TreeNode myBuildTree(int[] preorder, int[] inorder, int preorder_left, int preorder_right, int inorder_left, int inorder_right) {
        if (preorder_left > preorder_right) return null;
        int preorder_root = preorder_left;
        TreeNode root = new TreeNode(preorder[preorder_root]);
        int inorder_root = dic.get(preorder[preorder_root]);
        //得到左子树节点的数目
        int sub_left_subtree = inorder_root - inorder_left;
        //**注意这里把preorder_left,preorder_right看作是一个新的前序数组**
        root.left = myBuildTree(preorder, inorder,
                preorder_left + 1, preorder_left + sub_left_subtree,
                inorder_left, inorder_root - 1);
        root.right = myBuildTree(preorder, inorder,
                preorder_left + sub_left_subtree + 1, preorder_right,
                inorder_root + 1, inorder_right);
        return root;
    }
}

class BuildTree2 {
    HashMap<Integer,Integer> dic = new HashMap();
    int[] preorder;
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        this.preorder = preorder;
        for (int i = 0; i < inorder.length; i++) {
            dic.put(inorder[i], i);
        }
        return recur(0, 0, inorder.length-1);
    }
    public TreeNode recur(int root, int left, int right) {
        if (left > right) return null;                //结束条件
        TreeNode node = new TreeNode(preorder[root]); //建立根节点
        int i = dic.get(preorder[root]);             //获得中间根节点，划分根节点、左子树、右子树
        node.left = recur(root + 1, left, i-1);//左子树递归
        node.right = recur(i-left+root+1, i + 1, right);//右子树递归
        // **这里i-left+root + 1 = 左子树长度+根节点索引+1**
        return node;
    }
}

