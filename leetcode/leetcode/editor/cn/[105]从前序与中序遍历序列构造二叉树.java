//根据一棵树的前序遍历与中序遍历构造二叉树。 
//
// 注意: 
//你可以假设树中没有重复的元素。 
//
// 例如，给出 
//
// 前序遍历 preorder = [3,9,20,15,7]
//中序遍历 inorder = [9,3,15,20,7] 
//
// 返回如下的二叉树： 
//
//     3
//   / \
//  9  20
//    /  \
//   15   7 
// Related Topics 树 深度优先搜索 数组 
// 👍 1084 👎 0


//leetcode submit region begin(Prohibit modification and deletion)

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    private Map<Integer, Integer> indexMap;
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
        if (preorder_left > preorder_right) {
            return null;
        }
        //找到左子树中的根节点
        int preorder_root = preorder_left;
        //中序遍历中的根节点
        int inorder_root = indexMap.get(preorder[preorder_root]);
        //根节点
        TreeNode root = new TreeNode(preorder[preorder_root]);

        //得到左子树中的节点数目
        int size_left_subtree = inorder_root - inorder_left;

        root.left = myBuildTree(preorder, inorder,  preorder_left+1, preorder_left + size_left_subtree, inorder_left, inorder_root-1);
        root.right = myBuildTree(preorder, inorder, preorder_left + 1 + size_left_subtree, preorder_right, inorder_root+1 , inorder_right);
        return root;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
