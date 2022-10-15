package jianzhiOffer;

import javax.print.DocFlavor;
import java.util.LinkedList;
import java.util.List;

public class _34pathSum {
    List<List<Integer>> res = new LinkedList<>();
    LinkedList<Integer> path = new LinkedList<>();
    public List<List<Integer>> pathSum(TreeNode root, int target) {
        LinkedList<Integer> track = new LinkedList<>();
        backtrack(root,target);
        return res;
    }

    void backtrack(TreeNode root, int target) {
        if (root == null) return;
        path.add(root.val);
        target -= root.val;
        if (target == 0 && root.left == null && root.right == null) {
            res.add(new LinkedList<>(path));
        }
        backtrack(root.left, target);
        backtrack(root.right, target);
        path.removeLast();
    }

    //回溯算法
/*    void backtrack(TreeNode root, int target, List<Integer> track) {
        if () {
            result.add(路径);
        }
        for (选择 in 选择列表) {
            //做选择
            //将该选择从选择列表中移除
            //路径.add(选择)
            /acktrack(路径, 选择列表);
            //撤销选择
            //路径.remove(选择)
            //将该选择恢复到选择列表

        }
    }*/

}
