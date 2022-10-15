package ByteDance;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class _103 {
    public static void main(String[] args) {
        _103 v = new _103();
        TreeNode treeNode = new TreeNode(3);
        treeNode.left = new TreeNode(9);
        treeNode.right = new TreeNode(20);
        treeNode.right.left = new TreeNode(15);
        treeNode.right.right = new TreeNode(7);
        List<List<Integer>> lists = v.zigzagLevelOrder(treeNode);

        System.out.println(treeNode.val);
        System.out.print(treeNode.left.val + " ");
        System.out.println(treeNode.right.val);
        System.out.print(treeNode.right.left.val + " ");
        System.out.println(treeNode.right.right.val);

        for (List<Integer> list : lists) {
            for (Integer integer : list) {
                System.out.print(integer);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> res = new LinkedList<>();
        if (root == null) return res;
        Queue<TreeNode> que = new LinkedList();
        que.offer(root);
        int odd = 0;

        while (!que.isEmpty()) {
            odd++;
            int size = que.size();
            LinkedList<Integer> list = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                TreeNode cur = que.poll();
                if (odd % 2 == 1) {
                    list.add(cur.val);
                }else {
                    //list.add(0, cur.val);
                    list.addFirst(cur.val);
                }
                if (cur.left != null) {
                    que.offer(cur.left);
                }
                if (cur.right != null) {
                    que.offer(cur.right);
                }
            }
            res.add(list);
        }
        return res;
    }

}
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    public TreeNode() {

    }

    public TreeNode(int val) {
        this.val = val;
    }

    public TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
