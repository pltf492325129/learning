package jianzhiOffer;

import java.util.*;

public class _32levelOrder {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        Queue<TreeNode> que = new LinkedList<>();
        if (root != null) que.add(root);
        while (!que.isEmpty()) {
            /*???这里为啥只能i--; int i*/
            List<Integer> levelRes = new ArrayList<>();
            //for (int i=0; i < que.size();i++) {
            for (int i = que.size(); i > 0; i--) {
                TreeNode cur = que.poll();
                levelRes.add(cur.val);
                if (cur.left != null)  {
                    que.add(cur.left);
                }
                if (cur.right != null) {
                    que.add(cur.right);
                }
            }
            res.add(levelRes);
        }
        return res;
    }
    public List<List<Integer>> levelOrder2(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        Queue<TreeNode> que = new LinkedList<>();
        if (root != null) que.add(root);
        while (!que.isEmpty()) {
            LinkedList<Integer> levelRes = new LinkedList<>();
            for (int i = que.size(); i > 0; i--) {
                TreeNode cur = que.poll();
                if (res.size() % 2 == 0) levelRes.addLast(cur.val);
                else levelRes.addFirst(cur.val);
                if (cur.left != null)  que.add(cur.left);
                if (cur.right != null) que.add(cur.right);
            }
            res.add(levelRes);
        }
        return res;
    }

}
